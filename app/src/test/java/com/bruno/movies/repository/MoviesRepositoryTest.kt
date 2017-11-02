package com.bruno.movies.repository

import com.bruno.movies.api.MoviesService
import com.bruno.movies.api.PagingResponse
import com.bruno.movies.db.MovieDao
import com.bruno.movies.model.Movie
import com.bruno.movies.util.RecordingSubscriber
import com.bruno.movies.util.loadFromResource
import io.reactivex.Flowable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.verify


@RunWith(JUnit4::class)
class MoviesRepositoryTest {
    @Rule @JvmField
    val rule = RecordingSubscriber.Rule()

    lateinit var repository: MoviesRepository
    lateinit var dao: MovieDao
    lateinit var service: MoviesService

    @Before
    fun setup(){
        dao = Mockito.mock(MovieDao::class.java)
        service = Mockito.mock(MoviesService::class.java)

        repository = MoviesRepository("valid", dao, service)
    }

    @Test
    fun discoverEmptyCache(){
        val cache = listOf<Movie>()
        val cloud = loadFromResource<PagingResponse>("page1.json", this@MoviesRepositoryTest.javaClass.classLoader, PagingResponse::class.java)

        Mockito.`when`(service.discover(anyString(), anyInt(), anyString(), anyString(), anyString())).thenReturn(Observable.just(cloud))
        Mockito.`when`(dao.read(anyInt(), anyInt())).thenReturn(Flowable.merge(Flowable.just(cache), Flowable.just(cloud.results)))

        val subscriber = rule.create<List<Movie>>()
        repository.read(1).subscribe(subscriber)

        verify(dao).insertAll(any())
        subscriber.assertValue(cache).assertValue(cloud.results).assertComplete()
    }

    @Test
    fun discoverCache(){
        val cache = loadFromResource<PagingResponse>("page1.json", this@MoviesRepositoryTest.javaClass.classLoader, PagingResponse::class.java).results
        val cloud = loadFromResource<PagingResponse>("page1.json", this@MoviesRepositoryTest.javaClass.classLoader, PagingResponse::class.java)

        Mockito.`when`(service.discover(anyString(), anyInt(), anyString(), anyString(), anyString())).thenReturn(Observable.just(cloud))
        Mockito.`when`(dao.read(anyInt(), anyInt())).thenReturn(Flowable.merge(Flowable.just(cache), Flowable.just(cloud.results)))

        val subscriber = rule.create<List<Movie>>()
        repository.read(1).subscribe(subscriber)

        verify(dao).insertAll(any())
        subscriber.assertValue(cache).assertValue(cloud.results).assertComplete()
    }

    @Test
    fun discoverPage2(){
        val page1 = loadFromResource<PagingResponse>("page1.json", this@MoviesRepositoryTest.javaClass.classLoader, PagingResponse::class.java).results
        val page2 = loadFromResource<PagingResponse>("page2.json", this@MoviesRepositoryTest.javaClass.classLoader, PagingResponse::class.java)

        Mockito.`when`(service.discover(anyString(), anyInt(), anyString(), anyString(), anyString())).thenReturn(Observable.just(page2))
        Mockito.`when`(dao.read(anyInt(), anyInt())).thenReturn(Flowable.merge(Flowable.just(page1), Flowable.just(page2.results)))

        val subscriber = rule.create<List<Movie>>()
        repository.read(1).subscribe(subscriber)

        verify(dao).insertAll(any())
        subscriber.assertValue(page1).assertValue(page2.results).assertComplete()
    }

    @Test
    fun details(){
        val cache = loadFromResource<Movie>("details.json", this@MoviesRepositoryTest.javaClass.classLoader, Movie::class.java)
        val cloud = loadFromResource<Movie>("details.json", this@MoviesRepositoryTest.javaClass.classLoader, Movie::class.java)

        Mockito.`when`(service.details(anyString(), anyString(), anyString())).thenReturn(Observable.just(cloud))
        Mockito.`when`(dao.read(anyString())).thenReturn(Flowable.merge(Flowable.just(cache), Flowable.just(cloud)))

        val subscriber = rule.create<Movie>()
        repository.read("1").subscribe(subscriber)

        verify(dao).insert(any())
        subscriber.assertValue(cache).assertValue(cloud).assertComplete()
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}
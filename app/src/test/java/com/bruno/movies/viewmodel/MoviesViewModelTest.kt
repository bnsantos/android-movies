package com.bruno.movies.viewmodel

import com.bruno.movies.api.PagingResponse
import com.bruno.movies.repository.MoviesRepository
import com.bruno.movies.util.RecordingObserver
import com.bruno.movies.util.loadFromResource
import com.bruno.movies.vo.MoviesResource
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentMatchers.eq

@RunWith(JUnit4::class)
class MoviesViewModelTest {
    @Rule
    @JvmField
    val rule = RecordingObserver.Rule()

    lateinit var viewModel: MoviesViewModel
    lateinit var repository: MoviesRepository

    @Before
    fun setup(){
        repository = Mockito.mock(MoviesRepository::class.java)

        viewModel = MoviesViewModel(repository)
    }

    @Test
    fun error(){
        Mockito.`when`(repository.read(anyInt())).thenReturn(Flowable.error(Exception("mock error")))

        val subscriber = rule.create<MoviesResource>()
        viewModel.read(1).subscribe(subscriber)

        val value = subscriber.takeValue()
        assert(value is MoviesResource.Error)
        val error = value as MoviesResource.Error
        assert(error.err is Exception)
        assertThat(error.err.message).isEqualTo("mock error")

        subscriber.assertComplete()
    }

    @Test
    fun page1(){
        val data = loadFromResource<PagingResponse>("page1.json", this@MoviesViewModelTest.javaClass.classLoader, PagingResponse::class.java)
        Mockito.`when`(repository.read(anyInt())).thenReturn(Flowable.just(data.results))

        val subscriber = rule.create<MoviesResource>()
        viewModel.read(1).subscribe(subscriber)

        subscriber.assertValue(MoviesResource.Items(data = data.results)).assertComplete()
    }

    @Test
    fun pages(){
        val data1 = loadFromResource<PagingResponse>("page1.json", this@MoviesViewModelTest.javaClass.classLoader, PagingResponse::class.java)
        val data2 = loadFromResource<PagingResponse>("page2.json", this@MoviesViewModelTest.javaClass.classLoader, PagingResponse::class.java)
        Mockito.`when`(repository.read(eq(1))).thenReturn(Flowable.just(data1.results))
        Mockito.`when`(repository.read(eq(2))).thenReturn(Flowable.just(data2.results))

        val subscriberPage1 = rule.create<MoviesResource>()
        viewModel.read(1).subscribe(subscriberPage1)
        subscriberPage1.assertValue(MoviesResource.Items(data = data1.results)).assertComplete()

        viewModel.read(2).subscribe(subscriberPage1)
        subscriberPage1.assertValue(MoviesResource.Items(data = data2.results)).assertComplete()
    }

}
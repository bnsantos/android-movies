package com.bruno.movies.api

import com.bruno.movies.model.Movie
import com.bruno.movies.util.RecordingObserver
import com.bruno.movies.util.loadFromResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

@RunWith(JUnit4::class)
class MoviesServiceTest {
    @Rule @JvmField
    val mockWebServer: MockWebServer = MockWebServer()
    @Rule @JvmField
    val rule = RecordingObserver.Rule()

    private lateinit var service: MoviesService

    @Before
    @Throws(IOException::class)
    fun createService() {
        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create<MoviesService>(MoviesService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class)
    fun discoverOk() {
        enqueueResponse("page1.json")
        val subscriber = rule.create<PagingResponse>()

        service.discover("valid", 1, "release").subscribe(subscriber)
        val data = loadFromResource<PagingResponse>("page1.json", this@MoviesServiceTest.javaClass.classLoader, PagingResponse::class.java)

        println(data)

        subscriber.assertValue(data).assertComplete()
    }

    @Test
    fun discover401() {
        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody("{\n" +
                "  \"status_code\": 7,\n" +
                "  \"status_message\": \"Invalid API key: You must be granted a valid key.\",\n" +
                "  \"success\": false\n" +
                "}"))
        val subscriber = rule.create<PagingResponse>()
        service.discover("invalid", 1, "release").subscribe(subscriber)

        subscriber.assertError(HttpException::class.java, "HTTP 401 Client Error")
    }

    @Test
    fun discover422() {
        mockWebServer.enqueue(MockResponse().setResponseCode(422).setBody("{\n" +
                "  \"errors\": [\n" +
                "    \"page must be greater than 0\"\n" +
                "  ]\n" +
                "}"))
        val subscriber = rule.create<PagingResponse>()
        service.discover("valid", -1, "release").subscribe(subscriber)

        subscriber.assertError(HttpException::class.java, "HTTP 422 Client Error")
    }

    @Test
    fun discover400() {
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody("{\n" +
                "    \"status_code\": 34,\n" +
                "    \"status_message\": \"The resource you requested could not be found.\"\n" +
                "}"))
        val subscriber = rule.create<PagingResponse>()
        service.discover("valid", 1, "release").subscribe(subscriber)

        subscriber.assertError(HttpException::class.java, "HTTP 400 Client Error")
    }

    @Test
    @Throws(IOException::class)
    fun detailsOk() {
        enqueueResponse("details.json")
        val subscriber = rule.create<Movie>()

        service.details("1", "valid").subscribe(subscriber)
        val data = loadFromResource<Movie>("details.json", this@MoviesServiceTest.javaClass.classLoader, Movie::class.java)

        subscriber.assertValue(data).assertComplete()
    }

    @Test
    fun details404() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("{\n" +
                "  \"status_code\": 34,\n" +
                "  \"status_message\": \"The resource you requested could not be found.\"\n" +
                "}"))
        val subscriber = rule.create<PagingResponse>()
        service.discover("valid", 1, "release").subscribe(subscriber)

        subscriber.assertError(HttpException::class.java, "HTTP 404 Client Error")
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        enqueueResponse(fileName, emptyMap())
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String, headers: Map<String, String>) {
        val inputStream = javaClass.classLoader.getResourceAsStream("api-response/" + fileName)
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(mockResponse.setBody(source.readString(Charset.forName("UTF-8"))))
    }
}
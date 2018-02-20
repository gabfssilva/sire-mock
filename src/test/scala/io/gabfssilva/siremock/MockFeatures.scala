package io.gabfssilva.siremock

import org.scalatest.{BeforeAndAfter, FeatureSpec, Matchers}

import scalaj.http.{Http, StringBodyConnectFunc}

class MockFeatures
  extends FeatureSpec
    with Matchers
    with SireMock
    with BeforeAndAfter {

  override val sireMockConfig: SireMockConfig = SireMockConfig(port = 8181)

  before {
    startSireMock
    resetSireMock
  }

  after {
    stopSireMock
  }

  feature("GET") {
    scenario("basic mocking") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockGet(
        path = "/hello",
        withResponseBody = Some(expectedResponseBody)
      )

      val response = Http("http://localhost:8181/hello")
        .method("get")
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 200
    }

    scenario("basic verifying") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockGet(
        path = "/hello-verified",
        withResponseBody = Some(expectedResponseBody)
      )

      val response = Http("http://localhost:8181/hello-verified")
        .method("get")
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 200

      verifyGet("/hello-verified", count = 1.exactlyStrategy)
    }

    scenario("basic authentication") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockGet(
        path = "/hello-auth",
        withResponseBody = Some(expectedResponseBody),
        withBasicAuth = Some("user" -> "pass")
      )

      val response = Http("http://localhost:8181/hello-auth")
        .method("get")
        .auth("user", "pass")
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 200
    }
  }

  feature("POST") {
    scenario("basic mocking") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockPost(
        path = "/hello",
        requestBodyMatching = """{"hi":"you"}""",
        contentType = Some("application/json"),
        withResponseBody = Some(expectedResponseBody),
        withResponseStatus = 201
      )

      val response = Http("http://localhost:8181/hello")
        .header("Content-Type", "application/json")
        .postData("""{"hi":"you"}""")
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 201
    }
  }

  feature("PUT") {
    scenario("basic mocking") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockPut(
        path = "/hello",
        requestBodyMatching = """{"hi":"you"}""",
        contentType = Some("application/json"),
        withResponseBody = Some(expectedResponseBody)
      )

      val response = Http("http://localhost:8181/hello")
        .header("Content-Type", "application/json")
        .put("""{"hi":"you"}""")
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 200
    }
  }

  feature("DELETE") {
    scenario("basic mocking") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockDelete(
        path = "/hello",
        withResponseBody = Some(expectedResponseBody)
      )

      val response = Http("http://localhost:8181/hello").method("delete")
//        .copy(connectFunc=StringBodyConnectFunc(""))
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 200
    }
  }

  feature("PATCH") {
    scenario("basic mocking") {
      val expectedResponseBody = """{"hello":"world"}"""

      mockPatch(
        path = "/hello",
        requestBodyMatching = """{"hi":"you"}""",
        contentType = Some("application/json"),
        withResponseBody = Some(expectedResponseBody),
      )

      val response = Http("http://localhost:8181/hello")
        .header("Content-Type", "application/json")
        .copy(connectFunc=StringBodyConnectFunc("""{"hi":"you"}"""))
        .method("PATCH")
        .asString

      response.body shouldBe expectedResponseBody
      response.code shouldBe 200
    }
  }
}

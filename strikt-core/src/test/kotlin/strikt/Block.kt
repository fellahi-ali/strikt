package strikt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThan
import strikt.assertions.isNotNull
import strikt.assertions.isNull

@DisplayName("assertions in blocks")
internal class Block {
  @Test
  fun `all assertions in a block are evaluated even if some fail`() {
    assertThrows<AssertionError> {
      val subject: Any? = "fnord"
      expectThat(subject) {
        isNull()
        isNotNull()
        isA<String>()
        isA<Number>()
      }
    }.let { error ->
      val expected = """
        |▼ Expect that "fnord":
        |  ✗ is null
        |  ✓ is not null
        |  ✓ is an instance of java.lang.String
        |  ✗ is an instance of java.lang.Number : found java.lang.String"""
        .trimMargin()
      assertEquals(expected, error.message)
    }
  }

  @Test
  fun `chains inside of blocks break on the first failure`() {
    assertThrows<AssertionError> {
      val subject: Any? = "fnord"
      expectThat(subject) {
        isNotNull()
        isA<Number>().isA<Long>()
        isEqualTo("fnord")
      }
    }.let { error ->
      val expected = """
        |▼ Expect that "fnord":
        |  ✓ is not null
        |  ✗ is an instance of java.lang.Number : found java.lang.String
        |  ✓ is equal to "fnord""""
        .trimMargin()
      assertEquals(expected, error.message)
    }
  }

  @Test
  fun `get chained after a failing assertion is not evaluated`() {
    assertThrows<AssertionError> {
      val subject: Any? = "fnord"
      expectThat(subject) {
        isA<Int>().get("multiplied by two") { this * 2 }.isGreaterThan(1)
      }
    }.let { error ->
      val expected = """
        |▼ Expect that "fnord":
        |  ✗ is an instance of java.lang.Integer : found java.lang.String"""
        .trimMargin()
      assertEquals(expected, error.message)
    }
  }

  @Test
  fun `assertions in a block can be negated`() {
    assertThrows<AssertionError> {
      val subject: Any? = "fnord"
      expectThat(subject) {
        not().isNull()
        not().isNotNull()
        not().isA<String>()
        not().isA<Number>()
      }
    }.let { error ->
      val expected = """
        |▼ Expect that "fnord":
        |  ✓ is not null
        |  ✗ is null
        |  ✗ is not an instance of java.lang.String
        |  ✓ is not an instance of java.lang.Number"""
        .trimMargin()
      assertEquals(expected, error.message)
    }
  }

  @Test
  fun `assertions in a block can be negated in a not block`() {
    assertThrows<AssertionError> {
      val subject: Any? = "fnord"
      expectThat(subject).not {
        isNull()
        isNotNull()
        isA<String>()
        isA<Number>()
      }
    }.let { error ->
      val expected = """
        |▼ Expect that "fnord":
        |  ✓ is not null
        |  ✗ is null
        |  ✗ is not an instance of java.lang.String
        |  ✓ is not an instance of java.lang.Number"""
        .trimMargin()
      assertEquals(expected, error.message)
    }
  }

  @Test
  fun `an and block can be negated`() {
    val subject: Any? = "fnord"
    expectThat(subject).not().and {
      isNull()
    }
  }

  @Test
  fun `contains can be negated`() {
    expectThat(listOf<String>()).not().contains("blah")
  }
}

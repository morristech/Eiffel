package com.etiennelenhart.eiffel.binding.extension

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.etiennelenhart.eiffel.binding.BindableState
import com.etiennelenhart.eiffel.binding.bindableMapping
import com.etiennelenhart.eiffel.state.State
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class StateToBindableTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    data class TestState(val count: Int = 0) : State

    data class BindableTestState(val emptyStateVisible: Boolean = false) : BindableState

    private val mapping = bindableMapping<TestState, BindableTestState> { state ->
        copy(emptyStateVisible = state.count == 0)
    }

    @Test
    fun `GIVEN LiveData with State WHEN value queried THEN initial value is returned`() {
        val expected = BindableTestState()
        val state = MutableLiveData<TestState>()

        val actual = state.toBindable(mapping).value

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN LiveData with State WHEN value changed THEN mapped value is emitted`() {
        val state = MutableLiveData<TestState>().apply { value = TestState() }

        var actual = true
        state.toBindable(mapping).observeForever { actual = it.emptyStateVisible }
        state.value = state.value!!.copy(count = 1)

        assertFalse(actual)
    }
}

package com.gotalk.app

import com.gotalk.app.ui.components.SwipeDirection
import com.gotalk.app.ui.components.resolveSwipeDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SwipeDragTest {
    private val width = 400f

    @Test
    fun overThresholdRightAccepts() {
        assertEquals(SwipeDirection.Accept, resolveSwipeDirection(offsetX = 160f, velocityXDp = 0f, width = width))
    }

    @Test
    fun overThresholdLeftRejects() {
        assertEquals(SwipeDirection.Reject, resolveSwipeDirection(offsetX = -160f, velocityXDp = 0f, width = width))
    }

    @Test
    fun underThresholdSlowReturnsToCenter() {
        assertNull(resolveSwipeDirection(offsetX = 100f, velocityXDp = 200f, width = width))
    }

    @Test
    fun fastFlingConfirmsBelowThreshold() {
        assertEquals(SwipeDirection.Accept, resolveSwipeDirection(offsetX = 60f, velocityXDp = 1500f, width = width))
        assertEquals(SwipeDirection.Reject, resolveSwipeDirection(offsetX = -60f, velocityXDp = -1500f, width = width))
    }

    @Test
    fun flingAgainstOffsetDoesNotConfirm() {
        assertNull(resolveSwipeDirection(offsetX = 60f, velocityXDp = -1500f, width = width))
    }

    @Test
    fun tinyOffsetWithFlingDoesNotConfirm() {
        assertNull(resolveSwipeDirection(offsetX = 10f, velocityXDp = 1500f, width = width))
    }

    @Test
    fun zeroWidthIsSafe() {
        assertNull(resolveSwipeDirection(offsetX = 100f, velocityXDp = 1500f, width = 0f))
    }
}

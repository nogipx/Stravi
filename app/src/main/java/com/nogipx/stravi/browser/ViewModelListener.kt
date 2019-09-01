package com.nogipx.stravi.browser

import androidx.lifecycle.*

class ViewModelListener <T> (
    private val lifecycleOwner: LifecycleOwner,
    private val liveData: LiveData<T>,
    private val connects: List<Observer<T>>
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeObservers() {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            connects.forEach { liveData.observe(lifecycleOwner, it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unSubscribeObservers() =
        connects.forEach { liveData.removeObserver(it) }
}
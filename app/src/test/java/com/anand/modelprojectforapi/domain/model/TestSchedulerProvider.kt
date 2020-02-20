package com.anand.modelprojectforapi.domain.model

import com.anand.modelprojectforapi.domain.SchedulerProviderType
import io.reactivex.schedulers.Schedulers

class TestSchedulerProvider : SchedulerProviderType {
    override fun io() = Schedulers.trampoline()
    override fun ui() = Schedulers.trampoline()
    override fun computation() = Schedulers.trampoline()
    override fun newThread() = Schedulers.trampoline()
}

/*
 * Copyright (C) 2019 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package com.skydoves.chamber.executor

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.annotation.RestrictTo
import java.util.concurrent.Executor

/**
 * A static class that serves as a central point to execute common tasks.
 *
 *
 *
 * @hide This API is not final.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class ArchTaskExecutor private constructor() : TaskExecutor() {

  private var mDelegate: TaskExecutor

  private val mDefaultTaskExecutor: TaskExecutor

  init {
    mDefaultTaskExecutor = DefaultTaskExecutor()
    mDelegate = mDefaultTaskExecutor
  }

  /**
   * Sets a delegate to handle task execution requests.
   *
   *
   * If you have a common executor, you can set it as the delegate and App Toolkit components will
   * use your executors. You may also want to use this for your tests.
   *
   *
   * Calling this method with `null` sets it to the default TaskExecutor.
   *
   * @param taskExecutor The task executor to handle task requests.
   */
  fun setDelegate(taskExecutor: TaskExecutor?) {
    mDelegate = taskExecutor ?: mDefaultTaskExecutor
  }

  override fun executeOnDiskIO(runnable: Runnable) {
    mDelegate.executeOnDiskIO(runnable)
  }

  override fun postToMainThread(runnable: Runnable) {
    mDelegate.postToMainThread(runnable)
  }

  override val isMainThread: Boolean
    get() = mDelegate.isMainThread

  companion object {
    @Volatile
    private var sInstance: ArchTaskExecutor? = null

    val mainThreadExecutor: Executor = Executor { command -> instance?.postToMainThread(command) }

    val ioThreadExecutor: Executor = Executor { command -> instance?.executeOnDiskIO(command) }

    /**
     * Returns an instance of the task executor.
     *
     * @return The singleton ArchTaskExecutor.
     */
    val instance: ArchTaskExecutor?
      get() {
        if (sInstance != null) {
          return sInstance
        }
        synchronized(ArchTaskExecutor::class.java) {
          if (sInstance == null) {
            sInstance = ArchTaskExecutor()
          }
        }
        return sInstance
      }
  }
}

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

package com.skydoves.chamber

import com.skydoves.chamber.content.ContentActivity
import com.skydoves.chamber.content.ContentRepository
import com.skydoves.chamber.content.ContentSecondActivity
import com.skydoves.chamber.content.ContentSecondRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class ContentRepositoryTest {

  private lateinit var repository: ContentRepository
  private lateinit var controller: ActivityController<ContentActivity>

  @Before
  fun initRepository() {
    this.controller = Robolectric.buildActivity(ContentActivity::class.java).create().start()
    val activity = controller.get()
    this.repository = ContentRepository(activity)
  }

  @After
  fun destroy() {
    Chamber.destroyStore()
  }

  @Test
  fun changeValuesTest() {
    assertThat(repository.id.value, `is`(0))
    assertThat(repository.title.value, `is`("myTitle"))
    assertThat(repository.content.value, `is`("myContent"))

    this.repository.changeValues()

    assertThat(repository.id.value, `is`(1))
    assertThat(repository.title.value, `is`("myTitle1"))
    assertThat(repository.content.value, `is`("myContent1"))

    assertThat(Chamber.store().getFieldScopeCacheSize(), `is`(1))
    assertThat(
      Chamber.store().getLifecycleObserverStackSize(
        repository.id.annotation
      ),
      `is`(1)
    )
  }

  @Test
  fun postValuesTest() {
    this.repository.resetValues()

    assertThat(repository.id.value, `is`(0))
    assertThat(repository.title.value, `is`("myTitle"))
    assertThat(repository.content.value, `is`("myContent"))

    repository.id.postValue(1)
    repository.title.postValue("myTitle1")
    repository.content.postValue("myContent1")

    assertThat(repository.id.value, `is`(1))
    assertThat(repository.title.value, `is`("myTitle1"))
    assertThat(repository.content.value, `is`("myContent1"))

    assertThat(Chamber.store().getFieldScopeCacheSize(), `is`(1))
    assertThat(
      Chamber.store().getLifecycleObserverStackSize(
        repository.id.annotation
      ),
      `is`(1)
    )
  }

  @Suppress("UNCHECKED_CAST")
  @Test
  fun observeValueTest() {
    this.repository.resetValues()

    assertThat(repository.id.value, `is`(0))
    assertThat(repository.title.value, `is`("myTitle"))
    assertThat(repository.content.value, `is`("myContent"))

    val idObserver: ChamberFieldObserver<Int> =
      mock(ChamberFieldObserver::class.java) as ChamberFieldObserver<Int>
    val titleObserver: ChamberFieldObserver<String> =
      mock(ChamberFieldObserver::class.java) as ChamberFieldObserver<String>
    val contentObserver: ChamberFieldObserver<String> =
      mock(ChamberFieldObserver::class.java) as ChamberFieldObserver<String>

    repository.id.observe(idObserver)
    repository.title.observe(titleObserver)
    repository.content.observe(contentObserver)

    this.repository.changeValues()

    verify(idObserver).onChanged(1)
    verify(titleObserver).onChanged("myTitle1")
    verify(contentObserver).onChanged("myContent1")
  }

  @Suppress("UNCHECKED_CAST")
  @Test
  fun shareLifecycleTest() {
    val controller2 = Robolectric.buildActivity(ContentSecondActivity::class.java).create().start()
    val activity = controller2.get()
    val repository2 = ContentSecondRepository(activity)
    Chamber.shareLifecycle(repository2, activity)

    assertThat(repository2.id.value, `is`(0))
    assertThat(repository2.title.value, `is`("myTitle"))
    assertThat(repository2.content.value, `is`("myContent"))

    val observer: ChamberFieldObserver<Int> =
      mock(ChamberFieldObserver::class.java) as ChamberFieldObserver<Int>
    repository2.id.observe(observer)

    repository2.changeValues()

    verify(observer).onChanged(2)
    assertThat(repository2.id.value, `is`(2))
    assertThat(repository2.title.value, `is`("myTitle2"))
    assertThat(repository2.content.value, `is`("myContent2"))

    assertThat(Chamber.store().getFieldScopeCacheSize(), `is`(1))
    assertThat(
      Chamber.store().getLifecycleObserverStackSize(
        repository.id.annotation
      ),
      `is`(2)
    )

    controller2.destroy()
    controller.resume()

    assertThat(Chamber.store().getFieldScopeCacheSize(), `is`(1))
    assertThat(
      Chamber.store().getLifecycleObserverStackSize(
        repository.id.annotation
      ),
      `is`(1)
    )

    assertThat(repository.id.value, `is`(2))
    assertThat(repository.title.value, `is`("myTitle2"))
    assertThat(repository.content.value, `is`("myContent2"))

    this.controller.destroy()

    assertThat(Chamber.store().getFieldScopeCacheSize(), `is`(0))
    assertThat(
      Chamber.store().getLifecycleObserverStackSize(
        repository.id.annotation
      ),
      `is`(0)
    )
  }
}

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

package com.skydoves.chamber.content

import androidx.lifecycle.LifecycleOwner
import com.skydoves.chamber.Chamber
import com.skydoves.chamber.ChamberField
import com.skydoves.chamber.annotation.ShareProperty

@ContentScope
class ContentRepository(
  private val lifecycleOwner: LifecycleOwner
) {

  @ShareProperty("id")
  var id = ChamberField(0)

  @ShareProperty("title")
  var title = ChamberField("myTitle")

  @ShareProperty("content")
  var content = ChamberField("myContent")

  init {
    Chamber.shareLifecycle(
      scopeOwner = this,
      lifecycleOwner = lifecycleOwner)
  }

  fun changeValues() {
    id.value = 1
    title.value = "myTitle1"
    content.value = "myContent1"
  }
}

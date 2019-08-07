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

package com.skydoves.chamberdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.chamber.Chamber
import com.skydoves.chamber.ChamberField
import com.skydoves.chamber.annotation.ShareProperty
import com.skydoves.chamberdemo.scope.UserScope

@UserScope
class ThirdActivity : AppCompatActivity() {

  @ShareProperty("nickname")
  private var username = ChamberField("skydoves")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_third)

    Chamber.shareLifecycle(this, this)
    username.observe { Log.e("Test", "data is changed! : $it") }

    Log.e("Test", username.value)

    username.postValue("skydoves on ThirdActivity")
  }
}

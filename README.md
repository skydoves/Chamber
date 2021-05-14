# Chamber

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=15"><img alt="API" src="https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://www.codacy.com/app/skydoves/Chamber?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=skydoves/Chamber&amp;utm_campaign=Badge_Grade"><img alt="API" src="https://api.codacy.com/project/badge/Grade/ad561fa4877b4f04ac65cb5bf162ad0d"/></a>
  <a href="https://github.com/skydoves/Chamber/actions/workflows/android.yml"><img alt="API" src="https://github.com/skydoves/Chamber/actions/workflows/android.yml/badge.svg"/></a>
  <a href="https://androidweekly.net/issues/issue-372"><img alt="Android Weekly" src="https://img.shields.io/badge/Android%20Weekly-%23372-orange"/></a>
  <a href="https://skydoves.github.io/libraries/chamber/javadoc/chamber/index.html"><img alt="API" src="https://img.shields.io/badge/Javadoc-Chamber-yellow.svg"/></a>
</p>

<p align="center">
A lightweight Android thread-safe pipeline for communicating between lifecycle components with custom scopes.
</p>

> <p align="center">Android components are essential building blocks of an Android application. <br>These independent components are very loosely coupled. The benefit is that they are really independently reusable,<br> but it makes to hard communicate with each other. </p>

><p align="center"> The goal of this library is making easier to communicate and flow data with each other component like Activity, Fragment, Services, etc. And we can deliver data on each component easily and clear data on memory automatically following lifecycles. Also using custom scopes that are lifecycle aware makes developers can designate scoped data holder on their taste.</p>

## When is useful?
>When we need to hold some immutable data and it needs to be synchronized as the same data at each other components. For example, there is `Activity A`, `Activity B`, `Activity C`. And we need to use the same data in all Activity A~C that can be changed. Then we should pass a parcelable data A to B and B to C and getting the changed data reversely through onActivityResult. 

>Then how about the communication with fragments? We can solve it by implementing an interface,  singleton pattern, observer pattern or etc, but the data flow would be quite complicated. Chamber helps to simplify those communications between Chamber scope owners.

<p align="center">
<img width="859" alt="chamber01" src="https://user-images.githubusercontent.com/24237865/61701682-86756780-ad79-11e9-9520-f6ed9003204a.png">
</p>

## Including in your project
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/chamber.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%chamber%22)
[![Jitpack](https://jitpack.io/v/skydoves/chamber.svg)](https://jitpack.io/#skydoves/chamber)

### Gradle 
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```Gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```
And add a dependency code to your module's build.gradle file.
```gradle
dependencies {
    implementation "com.github.skydoves:chamber:1.0.2"
}
```

## SNAPSHOT 
[![Chamber](https://img.shields.io/static/v1?label=snapshot&message=chamber&logo=apache%20maven&color=C71A36)](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/chamber/) <br>
Snapshots of the current development version of Chamber are available, which track [the latest versions](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/chamber/).
```Gradle
repositories {
   maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```

## Usage
Chamber is scoped data holder with custom scopes that are lifecycle aware. 
### ChamberScope
The basic usage is creating a customized scope annotation using a `@ChamberScope` annotation. <br>
`@ChamberScope` is used to build custom scopes that are lifecycle aware. Each scope is a temporal data holder that has `ChamberProperty` data and lifecycle stack. It should be annotated a class (activity, fragment, repository or any classes) that has `ChamberProperty` fields.
```kotlin
@ChamberScope
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScope
```

### ChamberProperty
ChamberProperty is an interactive class to the internal Chamber data holder and a lifecycleObserver <br>that can be observable.
It should be used with `@ShareProperty` annotation that has a key name. If we want to use the same synchronized value on the same custom scope and different classes, we should use the same key.

```kotlin
@ShareProperty("name") // name is a key name.
var username = ChamberProperty("skydoves") // ChamberProperty can be initialized with any object.
```

#### setValue
Using the `setValue` method, we can change the `ChamberProperty`'s value.
```kotlin
username.setValue("user name is changed")
```
#### postValue
Posts a task to a main thread to set the given value. So if you have a following code executed in the main thread:
```kotlin
username.postValue("a")
username.setValue("b")
```
The value `b` would be set at first and later the main thread would override it with the value `a`.<br>
If you called this method multiple times before a main thread executed a posted task, only the last value would be dispatched.

#### observe
We can observe the value is changed using the `observe` method.
```kotlin
username.observe { 
  log("data is changed to $it")
}
```

### ShareLifecycle
Chamber synchronizes the ChamberProperty that has the same scope and same key. <br>
Also pushes a lifecycleOwner to the Chamber's lifecycle stack.<br>
Here is an example that has _MainActivity_ and _SecondActivity_.

#### MainActivity
__Chamber__ will create a `@UserScope` data holder. <br>
when `Chamber.shareLifecycle` method called, the `name` field that has `nickname` key will be managed by Chamber and Chamber will observe the _MainActivity_'s lifecycle state.

```kotlin
@UserScope // custom scope
class MainActivity : AppCompatActivity() {

  @ShareProperty("nickname")
  private var name = ChamberProperty("skydoves")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = this)

    name.value = "name value is changed"

    startActivity(SecondActivity::class.java)
  }
}
```

#### MainActivity -> SecondActivity
_MainActivity_ starts _SecondActivity_ using startActivity. <br>__Chamber__ will observe the _SecondActivity_'s lifecycle state. And the `name` field's value on the <br>_SecondActivity_ will be updated by __Chamber__ when `shareLifecycle` method called.
```kotlin
@UserScope
class SecondActivity : AppCompatActivity() {

  @ShareProperty("nickname")
  private var name = ChamberProperty("skydoves")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_second)

    Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = this)

    // the value is "name value is changed". because it was set in MainActivity.
    log("name value is .. ${username.value}")

    name.value = "changed in SecondActivity"

    finish()
  }
}
```

### The process of exiting scope
<p align="center">
<img width="859" alt="chamber02" src="https://user-images.githubusercontent.com/24237865/61709290-d5c39400-ad89-11e9-8008-3466280439ec.png">
</p>

#### SeondActivity -> MainActivity
`finish` method called in _SecondActivity_ and we come back to the _MainActivity_. <br>when _SecondActivity_'s lifecycle state is `onDestroy`, __Chamber__ will not interact anymore with the _SecondActivity_'s `ChamberProperty` and not observe lifecycle state. <br>And when _MainActivity_'s lifecycle state is `onResume`, __Chamber__ will update the `ChamberProperty`'s value in _MainActivity_.
```kotlin
@UserScope
class MainActivity : AppCompatActivity() {

  @ShareProperty("nickname")
  private var name = ChamberProperty("skydoves")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_second)
    
    // the value is "changed in SecondActivity". because it was set in SecondActivity.
    name.observe {
      log("name value is .. ${username.value}")
    }
  }
}
```
#### finish MainActivity
After all lifecycle owners are destroyed (all lifecycleOwners are popped from the __Chamber__'s lifecycle stack), the custom scope data space will be cleared in the internal data holder.

### Using on repository pattern
Architecturally, UI components should do work relate to UI works.<br>So it is more preferred to implement Chamber scope class on repository class.

```kotlin
@UserScope // custom scope
class MainActivityRepository(lifecycleOwner: LifecycleOwner) {

  @ShareProperty("nickname")
  var name = ChamberProperty("skydoves")

  init {
    // inject field data and add a lifecycleOwner to the UserScope scope stack.
    Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = lifecycleOwner)
  }
}

class MainActivity : AppCompatActivity() {

  private val repository = MainActivityRepository(this)

  // ...
}
```

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/chamber/stargazers)__ for this repository. :star:

## Design License
I designed flowcharts using [UXFlow](https://uxflow.co/), it is following [Attribution 4.0 International (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/legalcode).

# License
```xml
Copyright 2019 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

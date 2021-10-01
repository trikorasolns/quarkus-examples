package com.trikorasolutions.example;

import com.trikorasolutions.example.resource.UserResourceTest;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeGreetingResourceIT extends UserResourceTest {

    // Execute the same tests but in native mode.
}
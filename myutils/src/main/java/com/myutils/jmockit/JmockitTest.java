package com.myutils.jmockit;

import java.io.IOException;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

public class JmockitTest {

	 @Tested ServiceAbc tested;
	   @Injectable DependencyXyz mockXyz;

	   @Test
	   public void doOperationAbc(@Mocked final AnotherDependency anyInstance) {
	      new Expectations() {{
	         anyInstance.doSomething(anyString); result = 123;
	         AnotherDependency.someStaticMethod(); result = new IOException();
	      }};

	      tested.doOperationAbc("some data");

	      new Verifications() {{ mockXyz.complexOperation(true, anyInt, null); times = 1; }};
	   }
	
}

package common.http.interceptor;

import common.http.error.ForwardOnError;
import common.http.error.ForwardOnErrorInterceptor;
import common.http.error.JSONError;
import common.http.error.JSONErrorInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@JSONError
@ForwardOnError("test")
class ServletContainerInitializerTest {

    @Test
    void onStartup() {
        Set<Class<?>> clazzes = Set.of(JSONErrorInterceptor.class, ForwardOnErrorInterceptor.class);
        ServletContext sctx = Mockito.mock(ServletContext.class);
        try {
            new ServletContainerInitializer().onStartup(clazzes, sctx);
        }catch(IllegalArgumentException e){
            
        }
        assertNotNull(ServletInterceptorFactory.instantiate(getClass().getAnnotation(JSONError.class)));
        assertNotNull(ServletInterceptorFactory.instantiate(getClass().getAnnotation(ForwardOnError.class)));
        assertNull(ServletInterceptorFactory.instantiate(getClass().getAnnotation(TestInstance.class)));
    }

    @Test
    void onStartupCastFailed(){
        Set<Class<?>> clazzes = Set.of(ArrayList.class);
        ServletContext sctx = Mockito.mock(ServletContext.class);
        assertThrows(RuntimeException.class,() -> new ServletContainerInitializer().onStartup(clazzes, sctx));
    }
}
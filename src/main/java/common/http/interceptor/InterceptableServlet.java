package common.http.interceptor;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * Estende la classe astratta {@link HttpServlet} per fornire supporto al meccanismo degli interceptor.
 *
 * È possibile applicare gli interceptor in due modi:
 * <ul>
 *     <li>Applicando l'annotazione associata all'interceptor desiderato sul metodo "doX" desiderato</li>
 *     <li>Applicando l'annotazione associata all'interceptor desiderato sulla classe interceptor. In questo modo,
 *     l'interceptor sarà applicato a tutti i metodi "doX" sovrascritti.</li>
 * </ul>
 *
 *
 * <pre>
*  <code>
 *     {@literal @}RequireAuthentication
 *     {@literal @}EnableLogging(SEVERE)
 *     private static class SampleInterceptableServlet extends InterceptableServlet{
 *         {@literal @}Override
 *         {@literal @}ErrorsAsJson
 *         protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
 *              ....
 *         }
 *     }
 * </code>
 * </pre>
 * @see ServletInterceptor
 * @see HttpServlet
 */
public abstract class InterceptableServlet extends HttpServlet {
    private static final Map<String, String> methods =
            Map.of(
                    "GET",     "doGet",
                    "POST",    "doPost",
                    "PUT",     "doPut",
                    "TRACE",   "doTrace",
                    "OPTIONS", "doOptions",
                    "DELETE",  "doDelete",
                    "HEAD",    "doHead"
            );

    private final Map<String, HttpServletBiConsumer> chains = new ConcurrentHashMap<>();

    private ServletInterceptor<?>[] getInterceptors(String httpMethodName) {
        String javaMethodName = methods.get(httpMethodName);
        if(javaMethodName == null){
            //Not a "doX" method
            throw new IllegalArgumentException();
        }

        Method method;
        try {
            method = this.getClass()
                    .getDeclaredMethod(javaMethodName, HttpServletRequest.class, HttpServletResponse.class);
        } catch (NoSuchMethodException e) {
            //method has not been overriden by this class.
            //return empty array
            return new ServletInterceptor[]{};
        }


        //get annotations and their respective interceptor
        return Stream.concat(Arrays.stream(getClass().getAnnotations()), Arrays.stream(method.getAnnotations()))
                .map(ServletInterceptorFactory::instantiate)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(ServletInterceptor::priority)) //stable ordering
                .toArray(ServletInterceptor[]::new);
    }

    private HttpServletBiConsumer buildChain(ServletInterceptor<?>[] interceptors, HttpServletBiConsumer target){

        //builds the chain backwards
        HttpServletBiConsumer current = target;
        for(int i = interceptors.length-1; i >= 0; i--){
            ServletInterceptor<?> interceptor = interceptors[i];

            HttpServletBiConsumer next = current;
            current = (req, res) -> interceptor.handle(req, res, next);
        }
        return current;
    }

    //unambiguous super.service (there are 2 service methods. java lambdas hate that.)
    private void superService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    protected final void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String httpMethod = req.getMethod();
        chains.computeIfAbsent(httpMethod, key -> buildChain(getInterceptors(key), this::superService)).handle(req,resp);
    }

    @Override
    public final void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        service((HttpServletRequest) req, (HttpServletResponse) res);
    }

}
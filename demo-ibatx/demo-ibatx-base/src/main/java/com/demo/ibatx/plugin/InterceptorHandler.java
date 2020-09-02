package com.demo.ibatx.plugin;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 原生拦截器执行
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        ),
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        )
})
public class InterceptorHandler implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorHandler.class);

    private List<InterceptorAdapter> interceptorAdapterList = new ArrayList<>();

    public List<InterceptorAdapter> getInterceptorAdapterList() {
        return interceptorAdapterList;
    }

    public void addInterceptorAdapter(List<InterceptorAdapter> interceptorAdapters) {
        if (Objects.nonNull(interceptorAdapters)) {
            this.interceptorAdapterList.addAll(interceptorAdapters);
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Method method = invocation.getMethod();
        MappedStatement mappedStatement = null;
        Map<String,Object> mapperMethodParam = null;
        for (Object obj : args) {
            if (obj instanceof MappedStatement) {
                mappedStatement = (MappedStatement)obj;
            }
            if (obj instanceof MapperMethod.ParamMap) {
                mapperMethodParam = (MapperMethod.ParamMap) obj;
            }
        }
        if (Objects.isNull(mappedStatement)) {
            return invocation.proceed();
        }

        List<InterceptorAdapter> interceptorAdapters = this.getInterceptorAdapterList();
        for (InterceptorAdapter interceptorAdapter : interceptorAdapters) {
            if (!interceptorAdapter.intercept(mappedStatement, mapperMethodParam)) {
                // 需要拦截返回为空
                return returnEmptyResult(method);
            }
        }
        return invocation.proceed();
    }

    private Object returnEmptyResult(Method method) {
        Class<?> returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType)) {
            return Collections.emptyList();
        } else if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)){
            return 0;
        }
        return null;
    }


    /**
     * 可以拦截的类有。目前只拦截{@link Executor}
     * {@link Executor},
     * {@link ParameterHandler},
     * {@link ResultSetHandler},
     * {@link StatementHandler}
     *
     * @param target 拦截对象
     * @return
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

}

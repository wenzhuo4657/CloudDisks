package cn.wenzhuo4657.aspect;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.domain.HttpeCode;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.utils.StringUtil;
import cn.wenzhuo4657.utils.VerifUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

import static org.springframework.cglib.core.Constants.*;


/**
 * @className: ${NAME}
 * @author: LXHYouth
 * @date: ${DATE} ${TIME}
 * @Version: 1.0
 * @description:
 */
@Aspect
@Component
public class GlobalOperatcion {
    private  final  static Logger logger=LoggerFactory.getLogger(GlobalOperatcion.class);
    @Pointcut("@annotation(cn.wenzhuo4657.annotation.Global_interceptor)")
    public  void app(){
    }

    @Before("app()")
    public  void before(JoinPoint point) throws SystemException, NoSuchMethodException {
        try{
            Object target=point.getTarget();
            Object[] arguments=point.getArgs();
            String methodName=point.getSignature().getName();
            Class <?> [] parameterTypes=((MethodSignature)point.getSignature()).getMethod().getParameterTypes();
            Method method=target.getClass().getMethod(methodName,parameterTypes);
            Global_interceptor interceptor=method.getAnnotation(Global_interceptor.class);
            if (null==interceptor){
                return;
            }
            if(interceptor.checkparams()){
//                如果开启了参数校验，就进行校验
                validateParams(method,arguments);
            }
        } catch (Exception e) {
            logger.info("全局拦截器异常：:{}",e);
            throw e;
        }


    }

    private void validateParams(Method method, Object[] arguments) {
        Parameter[] parameters= method.getParameters();
        for(int i=0;i<parameters.length;i++){
            Parameter parameter=parameters[i];
            Object value=arguments[i];
            VerifyParam ve=parameter.getAnnotation(VerifyParam.class);
            if (ve==null){
                continue;
            }
            if (parameter.getParameterizedType() instanceof Class &&
                    ((Class<?>) parameter.getParameterizedType()).equals(String.class)  ){
                checkValue(value,ve);
            }else{
                checkObjValue(parameter,value);
            }

        }


    }


    /**
    * @Author wenzhuo4657
    * @Description  匹配带有校验注解的字段，调用checkValue去实现校验功能
    * @Date 16:02 2024-03-17
    * @Param [parameter, value] value表示方法参数的实际对象
    * @return void
    **/
    private void checkObjValue(Parameter parameter, Object value) {
        try{
            String type=parameter.getParameterizedType().getTypeName();
            Class classz=Class.forName(type);
            Field[] fields=classz.getDeclaredFields();
            for (Field field: fields ){
                VerifyParam fieldVerify=field.getAnnotation(VerifyParam.class);
                if (fieldVerify==null){
                    continue;//如果该属性的字段没有标注对应注解的话，跳过
                }
                field.setAccessible(true);
                Object res=field.get(value);
                checkValue(res,fieldVerify);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
    * @Author wenzhuo4657
    * @Description 实际带有校验功能的方法
    * @Date 16:06 2024-03-17
    * @Param [res, fieldVerify]  res表示校验参数的真实值，fielVerify校验字段的校验注解
    * @return void
    **/
    private void checkValue(Object res, VerifyParam fieldVerify) {
        Boolean isEmpty= Objects.isNull(res)|| StringUtil.isEmpty(res.toString());
        Integer length= Objects.isNull(res)?0:res.toString().length();


        if (isEmpty&& fieldVerify.required()){
            throw  new SystemException(HttpeCode.params_No_Ok);
        }

        if (!isEmpty && (
                (fieldVerify.min() != -1 && length <fieldVerify.min()) ||
                        (fieldVerify.max() != -1 && length > fieldVerify.max()) )
        ){
            throw  new SystemException(HttpeCode.params_No_Ok);
        }

        if (!isEmpty
                && !StringUtil.isEmpty(fieldVerify.regex().getRegex())
                && VerifUtils.verify(fieldVerify.regex(),(String)res)
        ){
            throw  new SystemException(HttpeCode.params_No_Ok);
        }




    }
}

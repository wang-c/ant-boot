/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-08-22 11:53 创建
 */
package org.antframework.boot.bekit.servicelistener;

import org.antframework.boot.bekit.AntBekitException;
import org.antframework.common.util.facade.AbstractResult;
import org.antframework.common.util.facade.CommonResultCode;
import org.antframework.common.util.facade.Status;
import org.bekit.event.annotation.Listen;
import org.bekit.service.annotation.listener.ServiceListener;
import org.bekit.service.event.ServiceApplyEvent;
import org.bekit.service.event.ServiceExceptionEvent;

/**
 * result维护-服务监听器
 */
@ServiceListener(priority = 30)
public class ResultMaintainServiceListener {

    @Listen
    public void listenServiceApplyEvent(ServiceApplyEvent event) {
        Object result = event.getServiceContext().getResult();
        if (result instanceof AbstractResult) {
            initResult((AbstractResult) result);
        }
    }

    @Listen(priorityAsc = false)
    public void listenServiceExceptionEvent(ServiceExceptionEvent event) {
        Object result = event.getServiceContext().getResult();
        if (result instanceof AbstractResult) {
            Throwable throwable = event.getTargetException();
            if (throwable instanceof AntBekitException) {
                setResultByAntBekitException((AbstractResult) result, (AntBekitException) throwable);
            } else {
                setResultByUnknownException((AbstractResult) result, throwable);
            }
        }
    }

    // 初始化result
    private void initResult(AbstractResult result) {
        result.setStatus(Status.SUCCESS);
        result.setCode(CommonResultCode.SUCCESS.getCode());
        result.setMessage(CommonResultCode.SUCCESS.getMessage());
    }

    // 依据AntBekitException设置result
    private void setResultByAntBekitException(AbstractResult result, AntBekitException antBekitException) {
        result.setStatus(antBekitException.getStatus());
        result.setCode(antBekitException.getCode());
        result.setMessage(antBekitException.getMessage());
    }

    // 依据未知异常设置result
    private void setResultByUnknownException(AbstractResult result, Throwable throwable) {
        result.setStatus(Status.PROCESSING);
        result.setCode(CommonResultCode.UNKNOWN_ERROR.getCode());
        result.setMessage(throwable.getMessage());
    }
}

package uyun.show.server.domain.util;

import uyun.show.server.domain.dto.OperationUser;

public class OperUserContext {

    private static ThreadLocal<OperationUser> opercentreUserThreadLocal = new ThreadLocal<OperationUser>();

    public static OperationUser getUser() {
        return opercentreUserThreadLocal.get();
    }

    public static void setUser(OperationUser operationUser) {
        opercentreUserThreadLocal.set(operationUser);
    }

}
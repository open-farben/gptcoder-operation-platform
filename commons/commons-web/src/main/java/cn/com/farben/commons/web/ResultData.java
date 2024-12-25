package cn.com.farben.commons.web;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 统一rest接口的返回信息
 */
@Getter
public class ResultData<T> {
    private final String code;
    private final T data;
    private final String message;

    private ResultData(String code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static class Builder<T> {
        private String code;
        private T data;
        private String message;

        /**
         * 操作成功
         * @return 构造器自己
         */
        public Builder<T> ok() {
            this.code = ErrorCodeEnum.OK.getCode();
            this.message = ErrorCodeEnum.OK.getDescribe();
            return this;
        }

        /**
         * 设置返回数据
         * @param data 返回的数据
         * @return 构造器自己
         */
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        /**
         * 构建错误信息
         * @param errorCodeEnum 错误信息枚举
         * @return 构造器自己
         */
        public Builder<T> error(ErrorCodeEnum errorCodeEnum) {
            this.code = errorCodeEnum.getCode();
            this.message = errorCodeEnum.getDescribe();
            return this;
        }

        /**
         * 设置消息内容
         * @param message 返回的消息
         * @return 构造器自己
         */
        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 构建返回信息的对象
         * @return 返回的对象信息
         */
        public ResultData<T> build() {
            return new ResultData<>(code, data, message);
        }
    }
}

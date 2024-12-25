package cn.com.farben.gptcoder.operation.platform.dictionary.application.component;

/**
 * 全局锁组件，对需要加锁的地方，请使用
 * <code>
 * GenericResult<String> result = DistributedLockComponent singleDistributedLockComponent = new SingleDistributedLockComponent();
 * lockSupportComponent.lock();
 * try{
 * //do something
 * }final{
 * lockSupportComponent.release(result.getResponse());
 * }
 * </code>
 **/
public interface DistributedLockComponent {

    long expire = 10;
    /**
     * 获取全局锁
     *
     * @param prefix
     *         业务场景前缀，如发起新签“startNew”
     * @param expire
     *         锁超时时间，超过时间锁会自动释放，单位：秒
     */
    default String lock(String prefix, long expire) {
        return lockWith(prefix, expire);
    }

    /**
     * 获取全局锁
     *
     * @param expire 锁超时时间，超过时间锁会自动释放，单位：秒
     * @return String 加全局锁结果，成功则返回加锁的key,否则抛出异常
     */
    default String lock(long expire){
        return lock("",expire);
    }


    /**
     * 获取全局锁
     *
     * @param key 加锁关键字
     * @param expire 锁超时时间，超过时间锁会自动释放，单位：秒
     */
    String lockWith(String key, long expire) ;

    /**
     * 获取全局锁
     *
     * @param expire 锁超时时间，超过时间锁会自动释放，单位：秒
     */
    default String lockWith(long expire) {
        return lockWith("", expire);
    }

    /**
     * 释放锁方法
     * @param key 关键字
     * @return 结果
     */
    Boolean release(String key);
}

package com.tk.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @Author wuXiaoMing
 * @Date 2023/1/31 17:58
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        long l = System.currentTimeMillis();
        this.strictInsertFill(metaObject, "createTime", Long.class, l);
        this.strictInsertFill(metaObject, "updateTime", Long.class, l);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
    }


}

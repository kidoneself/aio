package com.kidoneself.aio.common.mabatis.handler;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;

import com.kidoneself.aio.common.core.base.entity.Entity;
import com.kidoneself.aio.common.core.base.entity.SuperEntity;
import com.kidoneself.aio.common.core.util.ContextUtil;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 自动填充时间字段
 *
 * @author YiiDii Wang
 * @create 2021-09-27 23:25
 */
public class PigeonMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.fillCreated(metaObject);
        this.fillUpdated(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.fillUpdated(metaObject);
    }

    private void fillCreated(MetaObject metaObject) {
        // 设置创建时间和创建人
        if (metaObject.getOriginalObject() instanceof SuperEntity) {
            SuperEntity entity = (SuperEntity) metaObject.getOriginalObject();
            if (entity.getCreateTime() == null) {
                this.setFieldValByName(Entity.CREATE_TIME, LocalDateTime.now(), metaObject);
            }
            if (entity.getCreatedBy() == null || entity.getCreatedBy().equals(0)) {
                this.setFieldValByName(Entity.CREATED_BY, ContextUtil.getUserId(), metaObject);
            }
        }
    }

    private void fillUpdated(MetaObject metaObject) {
        // 修改人 修改时间
        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            if (entity.getUpdatedBy() == null || entity.getUpdatedBy().equals(0)) {
                this.setFieldValByName(Entity.UPDATED_BY, ContextUtil.getUserId(), metaObject);
            }
            if (entity.getUpdateTime() == null) {
                this.setFieldValByName(Entity.UPDATE_TIME, LocalDateTime.now(), metaObject);
            }
        }
    }
}

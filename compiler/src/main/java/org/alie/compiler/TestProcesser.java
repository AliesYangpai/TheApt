package org.alie.compiler;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * 1.一个类继承自注解处理器，那么这个类就是注解处理器了，abstrctProcessor就是注解处理器
 * 2.注册注解处理器使用：  'com.google.auto.service:auto-service:1.0-rc2'AutoService
 * 3.声明允许注解处理器处理的注解:SupportedAnnotationTypes(创建一个注解啦)
 * 4.指定编译的java版本
 */
@AutoService(Process.class)
@SupportedAnnotationTypes({"org.alie.theapt.BindView"}) //这里写上全类名来表示要处理的注解
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TestProcesser extends AbstractProcessor {

    /**
     *
     * @param set 使用注解处理器允许处理的注解的节点结合（就是说这个注解处理器允许处理的是BindView，用在属性上
     *              所以这是个属性节点，当然如果在类上的注解就是 类节点，方法上的就是方法节点，bindView使用了
     *              几次，则set的节点集合中就有几个  ）
     * @param roundEnvironment 集合
     * @return true 则代表该注解处理器不会再处理注解了
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }
}

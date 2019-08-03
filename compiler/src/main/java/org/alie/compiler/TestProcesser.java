package org.alie.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.awt.peer.DialogPeer;
import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * 1.一个类继承自注解处理器，那么这个类就是注解处理器了，abstrctProcessor就是注解处理器
 * 2.注册注解处理器使用：  'com.google.auto.service:auto-service:1.0-rc2'AutoService
 * 3.声明允许注解处理器处理的注解:SupportedAnnotationTypes(创建一个注解啦)
 * 4.指定编译的java版本
 */
@AutoService(Process.class)
@SupportedAnnotationTypes({"org.alie.annotation.BindView"}) //这里写上全类名来表示要处理的注解
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TestProcesser extends AbstractProcessor {

    // 日志处理工具
    private Messager messager;
    // 文件工具，通过使用文件工具 来进行注解和代码转化
    private Filer filer;

    /**
     * 重写init方法来获取日志工具（注，这里不能使用log）
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    /**
     * @param set              使用注解处理器允许处理的注解的节点结合（就是说这个注解处理器允许处理的是BindView，用在属性上
     *                         所以这是个属性节点，当然如果在类上的注解就是 类节点，方法上的就是方法节点，bindView使用了
     *                         几次，则set的节点集合中就有几个  ）
     * @param roundEnvironment 集合
     * @return true 则代表该注解处理器不会再处理注解了
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // 遍历节点
        for (TypeElement typeElement : set) {
            // 这个步骤是 拿到textView的节点集合（仍需要确认）
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(typeElement);
            for (Element element : elements) {
                // 打印元素，注意，这里需要使用的是Diagnostic.Kind.NOTE，不能用error，否则就会错误终止
                messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName());
                // 获取父节点，这个tv的父节点当然就是 MainActivit啦
                Element enclosingElement = element.getEnclosingElement();
                String className = enclosingElement.getSimpleName() + "_Binding";

                // 获取执行绑定的String文本字符串 并写入到 build/generated/source/apt/debug之中
//                String javaContent = "package com.dongnao.apt;\n" +
//                        "\n" +
//                        "/**\n" +
//                        " * @author Lance\n" +
//                        " * @date 2018/3/22\n" +
//                        " */\n" +
//                        "\n" +
//                        "public class MainActivity_Binding {\n" +
//                        "\n" +
//                        "    public MainActivity_Binding(MainActivity mainActivity) {\n" +
//                        "        mainActivity.tv = mainActivity.findViewById(R.id.tv);\n" +
//                        "    }\n" +
//                        "}\n";

                // （当然了，这种拼接字符串的方法，肯定是不能够接受的，能把人折腾死，因此选用javaPoet）
                // javaPoet就是帮我们转化这些字符串的

                //   https://github.com/square/javapoet
                // 创建一个 main函数
                MethodSpec main = MethodSpec.methodBuilder("test")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC) // 定义访问修饰符
                        .returns(void.class) // 定义返回结果
                        .addParameter(String[].class, "args") // 定义参数
                        .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                        .build();

                // 创建一个类HelloWorld的类
                TypeSpec helloWorld = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL) // 定义访问作用域
                        .addMethod(main)  // 把上面那个main函数加进来
                        .build();
                // 实际生成一个java文件
                JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                        .build();

                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 将此字符串输入到指定文件中
        }
        return true;
    }
}

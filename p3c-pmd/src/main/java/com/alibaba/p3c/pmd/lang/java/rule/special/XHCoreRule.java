package com.alibaba.p3c.pmd.lang.java.rule.special;

import com.alibaba.p3c.pmd.lang.java.rule.comment.AbstractAliCommentRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import org.jaxen.JaxenException;

import java.util.List;

/**
 * 学海 XHCoreRule 检查
 * 1. XHApplication 类，
 * 1. 欢迎页面监测XHCore 是否存在，书写是否正确
 * 2. 检查 盒子配置 和管控开启
 */
public class XHCoreRule extends AbstractAliCommentRule {
    public XHCoreRule() {
        super();
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }


    /** 检查继承自XhBaseApplication的类，先检查方法 getAppConfig方法，再去检查 getMicroServerDefaultMap方法，
     *  再去检查 new HashMap()
     * @param node node
     * @param data data
     * @return obj
     */
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        String xpathExtendsList = "//ExtendsList[(ClassOrInterfaceType[@Image='XhBaseApplication'])]";
        // [1] 检查继承 XhBaseApplication，先决条件
        if (!node.hasDescendantMatchingXPath(xpathExtendsList)) {
            return super.visit(node, data);
        }
        String image = node.getImage();
        // [2] 检查是否存在 getAppConfig 方法
        try {
            String xpathString = "//MethodDeclaration[(MethodDeclarator[@Image='getAppConfig'])]";
            List<Node> childNodesWithXPath = node.findChildNodesWithXPath(xpathString);
            if (childNodesWithXPath.isEmpty()) {
                addViolationWithMessage(data, node, image + " 必须实现 getAppConfig 方法");
            } else if (childNodesWithXPath.size() > 1) {
                addViolationWithMessage(data, node, image + "  getAppConfig 方法一个即可");
            } else {
                // [3] 检查是否存在 getMicroServerDefaultMap 方法
                Node nodeTmp2 = childNodesWithXPath.get(0);
                String xpathString2 = "//MethodDeclaration[(MethodDeclarator[@Image='getMicroServerDefaultMap'])]";
                @SuppressWarnings("unchecked")
                List<Node> childNodesWithXPath2 = (List<Node>) nodeTmp2.findChildNodesWithXPath(xpathString2);
                if (childNodesWithXPath2.isEmpty()) {
                    addViolationWithMessage(data, node, image + " getAppConfig 方法里必须实现 getMicroServerDefaultMap 方法");
                } else if (childNodesWithXPath2.size() > 1) {
                    addViolationWithMessage(data, node, image + "  getMicroServerDefaultMap 方法一个即可");
                } else {
                    // [4] 检查getMicroServerDefaultMap 方法是否存在HashMap
                    Node nodeTmp3 = childNodesWithXPath2.get(0);
                    String xpathString3 = "//ClassOrInterfaceType[@Image='HashMap']";
                    @SuppressWarnings("unchecked")
                    List<Node> childNodesWithXPath3 = (List<Node>) nodeTmp3.findChildNodesWithXPath(xpathString3);
                    if (childNodesWithXPath3.isEmpty()) {
                        addViolationWithMessage(data, node, image + " getMicroServerDefaultMap 方法必须实现 new HashMap");
                    }
                }
                //[5] 检查getMicroServerVersion 方法
                String xpathStringGetMicroServerVersion = "//MethodDeclaration[(MethodDeclarator[@Image='getMicroServerVersion'])]";
                @SuppressWarnings("unchecked")
                List<Node> childNodesWithXPathForVersion = (List<Node>) nodeTmp2.findChildNodesWithXPath(xpathStringGetMicroServerVersion);
                if (childNodesWithXPathForVersion.isEmpty()) {
                    addViolationWithMessage(data, node, image + " getAppConfig 方法里必须实现 getMicroServerVersion 方法");
                } else if (childNodesWithXPathForVersion.size() > 1) {
                    addViolationWithMessage(data, node, image + "  getMicroServerVersion 方法一个即可");
                }
            }
        } catch (JaxenException e) {
            e.printStackTrace();
        }

        return super.visit(node, data);
    }
}

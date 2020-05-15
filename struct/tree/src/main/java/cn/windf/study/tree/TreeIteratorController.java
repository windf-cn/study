package cn.windf.study.tree;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/tree/iterator")
public class TreeIteratorController {

    TreeNode rootMiddle;
    TreeNode rootPre;
    TreeNode rootPost;

    public TreeIteratorController() {
        rootMiddle = new TreeNode(6);
        rootMiddle.left = new TreeNode(2);
        rootMiddle.left.left = new TreeNode(1);
        rootMiddle.left.right = new TreeNode(4);
        rootMiddle.left.right.left = new TreeNode(3);
        rootMiddle.left.right.right = new TreeNode(5);
        rootMiddle.right = new TreeNode(9);
        rootMiddle.right.left = new TreeNode(7);
        rootMiddle.right.left.right = new TreeNode(8);


        rootPre = new TreeNode(1);
        rootPre.left = new TreeNode(2);
        rootPre.left.left = new TreeNode(3);
        rootPre.left.right = new TreeNode(4);
        rootPre.left.right.left = new TreeNode(5);
        rootPre.left.right.right = new TreeNode(6);
        rootPre.right = new TreeNode(7);
        rootPre.right.left = new TreeNode(8);
        rootPre.right.left.right = new TreeNode(9);


        rootPost = new TreeNode(9);
        rootPost.left = new TreeNode(5);
        rootPost.left.left = new TreeNode(1);
        rootPost.left.right = new TreeNode(4);
        rootPost.left.right.left = new TreeNode(2);
        rootPost.left.right.right = new TreeNode(3);
        rootPost.right = new TreeNode(8);
        rootPost.right.left = new TreeNode(7);
        rootPost.right.left.right = new TreeNode(6);
    }

    private List<TreeNode> nodes = new ArrayList<>(9);

    @GetMapping("/recursion-middle")
    public String recursionMiddle() {
        recursionMiddle(rootMiddle);

        return "size:" + nodes.size();
    }

    private void middle(TreeNode root) {

        Deque<TreeNode> stack = new LinkedList<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.peek();

            while (node.left != null) {
                stack.push(node.left);
                node = node.left;
            }

        }
    }

    private void recursionMiddle(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }

        recursionMiddle(treeNode.left);
        nodes.add(treeNode);
        recursionMiddle(treeNode.right);
    }

    @GetMapping("/recursion-pre")
    public String recursionPre() {
        recursionPre(rootPre);

        return "size:" + nodes.size();
    }

    private void recursionPre(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }

        nodes.add(treeNode);
        recursionPre(treeNode.left);
        recursionPre(treeNode.right);
    }

    @GetMapping("/recursion-post")
    public String recursionPost() {
        recursionPost(rootPost);

        return "size:" + nodes.size();
    }

    private void recursionPost(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }

        recursionPost(treeNode.left);
        recursionPost(treeNode.right);
        nodes.add(treeNode);
    }

    public static void main(String[] args) {
        TreeIteratorController controller = new TreeIteratorController();
        controller.recursionPost();
//        controller.recursionPre();
//        controller.recursionMiddle();

        for (TreeNode n : controller.nodes) {
            for (int i = 1; i <= n.data; i++) {
                System.out.print("-");
            }
            System.out.println();
        }
    }

    class TreeNode {
        int data;
        TreeNode left;
        TreeNode right;

        TreeNode(int data) {
            this.data = data;
        }
    }

}

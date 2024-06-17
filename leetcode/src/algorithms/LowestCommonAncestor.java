package algorithms;

import algorithms.base.model.TreeNode;

/**
 * 二叉最近公共祖先
 *
 * @author samin
 * @date 2021-01-11
 */
public class LowestCommonAncestor {

    public static void main(String[] args) {
        TreeNode t8 = new TreeNode(8);
        TreeNode t6 = new TreeNode(6);
        TreeNode t7 = new TreeNode(7);
        TreeNode t4 = new TreeNode(4);
        TreeNode t0 = new TreeNode(0);
        TreeNode t1 = new TreeNode(1, t0, t8);
        TreeNode t2 = new TreeNode(2, t7, t4);
        TreeNode t5 = new TreeNode(5, t6, t2);
        TreeNode t3 = new TreeNode(3, t5, t1);
        TreeNode res = new LowestCommonAncestor().lowestCommonAncestor(t3, t4, t5);

        // 5
        System.out.println(res.val);
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // 1. 左右子树都不包含 p，q 节点
        if (left == null && right == null) {
            return null;
        }

        // 2. 说明最近公共祖先在右子树，先返回哪个说明哪个在偏上方
        if (left == null) {
            return right;
        }

        // 3. 说明最近公共祖先在左子树，先返回哪个说明哪个在偏上方
        if (right == null) {
            return left;
        }

        // 4. left != null && right != null
        // 说明在 root 的两边，结果即为 root
        return root;
    }
}

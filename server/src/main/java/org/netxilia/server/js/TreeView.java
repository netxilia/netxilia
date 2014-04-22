package org.netxilia.server.js;

import static org.stjs.javascript.Global.$map;

import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.Map;
import org.stjs.javascript.functions.Callback1;

public class TreeView {
	private TreeNode root;
	private TreeNode crtNode;
	public Map<String, TreeNode> nodes;

	public TreeView() {
		this.root = new TreeNode(null, -1, null, Global.<TreeNode> $array(), null);
		this.crtNode = this.root;
		this.nodes = $map();
	}

	public void node(int level, String key, Object data) {
		TreeNode p = null;
		for (p = this.crtNode; p.parent != null && level <= p.level; p = p.parent) {
		}
		TreeNode n = new TreeNode(key, p.level + 1, data, Global.<TreeNode> $array(), p);
		p.children.push(n);
		this.nodes.$put(key, n);
		this.crtNode = n;
	}

	public void walk(Callback1<TreeNode> callback, TreeNode from) {
		TreeNode n = from != null ? from : this.root;
		for (int c : n.children) {
			TreeNode nc = n.children.$get(c);
			callback.$invoke(nc);
			this.walk(callback, nc);
		}
	}

	public static class TreeNode {
		public String key;
		public int level;
		public Object data;
		public Array<TreeNode> children;
		public TreeNode parent;

		public TreeNode(String key, int level, Object data, Array<TreeNode> children, TreeNode parent) {
			this.key = key;
			this.level = level;
			this.data = data;
			this.children = children;
			this.parent = parent;
		}

	}
}

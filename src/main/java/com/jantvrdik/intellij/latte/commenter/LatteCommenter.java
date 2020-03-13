package com.jantvrdik.intellij.latte.commenter;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * Implements support for action "Comments with Block Comment".
 */
public class LatteCommenter implements Commenter {
	@Nullable
	@Override
	public String getLineCommentPrefix() {
		return null;
	}

	@Nullable
	@Override
	public String getBlockCommentPrefix() {
		return "{*";
	}

	@Nullable
	@Override
	public String getBlockCommentSuffix() {
		return "*}";
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentPrefix() {
		return null;
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentSuffix() {
		return null;
	}
}

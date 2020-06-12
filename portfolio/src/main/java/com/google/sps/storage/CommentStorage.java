package com.google.sps.storage;

import com.google.sps.data.Comment;
import java.util.List;

public interface CommentStorage {
  void insert(Comment comment);

  List<Comment> listComments(int numComments, boolean sortAsc);

  void deleteAll();
}
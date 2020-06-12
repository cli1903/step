package com.google.sps.storage;

import java.util.List;
import com.google.sps.data.Comment;


public interface CommentStorage {

  void insert(Comment comment);

  List<Comment> listComments(int numComments, boolean sortAsc);

  void deleteAll();
}
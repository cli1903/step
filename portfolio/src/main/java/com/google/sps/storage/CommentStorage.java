package com.google.sps.storage;

import java.util.ArrayList;
import java.util.List;
import com.google.sps.data.Comment;


public interface CommentStorage {

  void insert(Comment comment);

  ArrayList<Comment> getNComments(int numComments, boolean sortAsc);

  void deleteAll();
}
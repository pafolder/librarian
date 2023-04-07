package com.pafolder.librarian.domain.command;

@FunctionalInterface
public interface Command<T> {

  T execute();
}

package com.pafolder.librarian.application.command;

@FunctionalInterface
public interface Command<T> {

  T execute();
}

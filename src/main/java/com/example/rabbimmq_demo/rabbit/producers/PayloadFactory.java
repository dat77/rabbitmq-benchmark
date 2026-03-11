package com.example.rabbimmq_demo.rabbit.producers;

import java.util.Arrays;

public class PayloadFactory {

  public static String payloadOfSize(int bytes) {
    char[] chars = new char[bytes];
    Arrays.fill(chars, '*');
    return new String(chars);
  }


}

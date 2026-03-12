package com.example.rabbimmq_demo.rabbit.producers;

import java.util.Random;
import java.util.stream.IntStream;

public class PayloadFactory {

  public static String payloadOfSize(int bytes) {
    char[] chars = new char[bytes];
    var random = new Random();
    IntStream.range(0, chars.length - 1)
        .forEach( i -> chars[i] = (char) ('a' + random.nextInt(26)));
    return new String(chars);
  }


}

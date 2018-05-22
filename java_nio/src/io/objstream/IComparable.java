package io.objstream;

public interface IComparable {
        int getAge();

        String getName();

        default double getPrice() {
            return 0;
        }

        default double getHeight() {
            return 0;
        }

    }

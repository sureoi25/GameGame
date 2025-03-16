package org.example.utils;

public class constants {


    public static class Directions{
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;

    }

    public static class PlayerConstants{
        public static final int RUNNING_DOWN = 3;
        public static final int RUNNING_SIDE = 4;
        public static final int RUNNING_UP = 5;
        public static final int IDLE_DOWN = 0;
        public static final int IDLE_SIDE = 1;
        public static final int IDLE_UP = 2;
        public static final int ATTACK_DOWN = 6;
        public static final int ATTACK_SIDE = 7;
        public static final int ATTACK_UP = 8;
        public static final int DIE = 9;

        public static int GetSpriteAmount(int playerAction){

            switch (playerAction){

                case RUNNING_DOWN:
                case RUNNING_SIDE:
                case RUNNING_UP:
                case IDLE_DOWN:
                case IDLE_SIDE:
                case IDLE_UP:
                    return 6;
                case ATTACK_DOWN:
                case ATTACK_SIDE:
                case ATTACK_UP:
                    return 4;
                case DIE:
                    return 3;
                default:
                    return 1;
            }
        }
    }
}

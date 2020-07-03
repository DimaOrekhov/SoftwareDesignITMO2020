package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.parsing.domain.AutomatonInputStream;

import java.util.Iterator;

/**
 * AutomatonInputStream of single Characters
 * Allows to iterate over single Characters of underlying String
 * Iterator position is the same for every instance StringPosStreamIterator
 * associated with a single StringPosStream instance.
 * Allows to move iterators' position up the stream.
 */
public class StringPosStream implements AutomatonInputStream<Character> {

    protected int currPos;
    protected final String sequence;

    public StringPosStream(String sequence) {
        this.sequence = sequence;
        currPos = 0;
    }

    @Override
    public Iterator<Character> iterator() {
        return new StringPosStreamIterator();
    }

    @Override
    public void rollBack() {
        if (currPos != 0) {
            currPos--;
        }
    }

    private class StringPosStreamIterator implements Iterator<Character> {

        @Override
        public boolean hasNext() {
            return currPos < sequence.length();
        }

        @Override
        public Character next() {
            Character element = sequence.charAt(currPos);
            currPos++;
            return element;
        }
    }

}

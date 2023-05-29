package work;

import java.util.Stack;

class CQueue {
    private Stack<Integer> inStack;
    private Stack<Integer> midStack;

    public CQueue() {
        inStack=new Stack<>();
        midStack=new Stack<>();
    }

    public void appendTail(int value) {
        inStack.add(value);
    }

    public int deleteHead() {
        int result;
        if(inStack.size()==0){
            return -1;
        }
        while (!inStack.empty()){
            int value=inStack.pop();
            midStack.add(value);
        }
        result=midStack.pop();
        while(!midStack.empty()){
            int value=midStack.pop();
            inStack.add(value);
        }
        return result;
    }
}

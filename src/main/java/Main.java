import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        new Thread(new ComputingServer(8085)).start();
        new Thread(new Client()).start();
    }
}

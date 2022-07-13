import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client implements Runnable {
    private final Scanner scanner;
    private final SocketChannel socketChannel;

    public Client() throws IOException {
        scanner = new Scanner(System.in);
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8085));
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Введите строку текста или end для завершения");
                String in = scanner.nextLine();
                socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                if (in.equalsIgnoreCase("end")) {
                    break;
                }
                System.out.println(read());
            }
            scanner.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read() throws IOException {
        ByteBuffer input = ByteBuffer.allocate(2 << 10);
        StringBuilder sb = new StringBuilder();
        int readBytes = socketChannel.read(input);
        socketChannel.configureBlocking(false);
        while (readBytes > 0) {
            sb.append(new String(input.array(), 0, readBytes, StandardCharsets.UTF_8));
            input.clear();
            readBytes = socketChannel.read(input);
        }
        socketChannel.configureBlocking(true);
        return sb.toString();
    }
}

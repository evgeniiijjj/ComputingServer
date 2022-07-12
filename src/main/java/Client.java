import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client implements Runnable {
    private final Scanner scanner;
    private final SocketChannel socketChannel;
    private final ByteBuffer input;

    public Client() throws IOException {
        scanner = new Scanner(System.in);
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8085));
        input = ByteBuffer.allocate(2 << 10);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Введите строку текста или end для завершения");
                String in = scanner.nextLine();
                if (in.equalsIgnoreCase("end")) {
                    socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                    break;
                }
                socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                int readBytes = socketChannel.read(input);
                System.out.println(new String(input.array(), 0, readBytes, StandardCharsets.UTF_8));
                input.clear();
            }
            scanner.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

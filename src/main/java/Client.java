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
                System.out.println("Введите порядковый номер числа или end для завершения");
                try {
                    String in = scanner.nextLine();
                    if (in.equalsIgnoreCase("end")) {
                        socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                        break;
                    }
                    int count = Integer.parseInt(in);
                    if (count == 0) throw new NumberFormatException();
                    socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                    int readBytes = socketChannel.read(input);
                    System.out.println(new String(input.array(), 0, readBytes, StandardCharsets.UTF_8));
                    input.clear();
                } catch (NumberFormatException e) {
                    System.out.println("Ввод некорректен повторите ввод");
                }
            }
            scanner.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

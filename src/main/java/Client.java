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
                System.out.println("Введите порядковый номер числа в последовательности чисел Фибоначчи или end для завершения");
                try {
                    String in = scanner.nextLine();
                    if (in.equalsIgnoreCase("end")) {
                        socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                        break;
                    }
                    int count = Integer.parseInt(in);
                    if (count == 0) throw new NumberFormatException();
                    socketChannel.write(ByteBuffer.wrap(in.getBytes()));
                    System.out.println(read());
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

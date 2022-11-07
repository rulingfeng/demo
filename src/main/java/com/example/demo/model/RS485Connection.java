//package com.example.demo.model;
//
//import lombok.extern.slf4j.Slf4j;
//import net.cnool.iot.commons.utils.FileUtil;
//import net.cnool.iot.door.basicEquipment.device.server.ReadSocketData;
//import net.cnool.iot.door.basicEquipment.device.server.SubDevicesServer;
//import net.cnool.iot.door.basicEquipment.utils.ByteUtil;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.util.UUID;
//
//@Slf4j
//public class RS485Connection implements Runnable {
//    /**
//     * 该线程唯一标识
//     */
//    private final String uuid;
//    /**
//     * 当前方法是否可执行
//     */
//    private boolean flag = true;
//
//    private SubDevicesServer subDevicesServer = new SubDevicesServer();
//
//
//    private final Socket socket;
//    /**
//     * 发送指令
//     */
//    private byte[] instruction;
//
//    /**
//     * 品牌名称
//     */
//    private String brand;
//
//    /**
//     * 读取socket最长不接受数据时间
//     */
//    private final long waitMaxTime = 60 * 8 * 1000;
//
//    /**
//     * 真实设备编号(485设备)
//     */
//    private final String deviceNo;
//
//    /**
//     * 是不是有效socket
//     */
//    private final boolean result;
//
//    /**
//     * socket接受数据最新时间
//     */
//    private long time = 0l;
//
//    public RS485Connection(Socket socket) {
//        this.uuid = UUID.randomUUID().toString();
//        StringBuffer readInstruction = new StringBuffer();
//
//        try {
//            long sartTime = System.currentTimeMillis();
//            InputStream input = socket.getInputStream();
//            int count = 0;
//            count = input.available();
//            while (count == 0) {
//                Thread.sleep(50);
//                count = input.available();
//                long waitTime = System.currentTimeMillis() - sartTime;
//                if (waitTime > 2 * 1000) {//2分钟没收到数据即无效socket
//                    break;
//                }
//                if (count == 0) {
//                    continue;
//                }
//            }
//
//            if (count != 0) {
//                int readCount = 0;
//                while (readCount < count) {
//                    readCount++;
//                    String hex = Integer.toHexString(input.read()).toUpperCase();
//                    if (hex.length() == 1) {
//                        readInstruction.append("0" + hex);
//                    } else {
//                        readInstruction.append(hex);
//                    }
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        this.instruction = null;
//        if (StringUtils.isBlank(readInstruction.toString())) {
//            result = false;
//            this.socket = socket;
//
//            this.deviceNo = "";
//
//        } else {
//            if (readInstruction.toString().length() == 8) {
//                this.socket = socket;
//                log.info("设备唯一编号" + readInstruction.toString());
//                this.deviceNo = readInstruction.toString();
//                SocketMap.allSockList.put(readInstruction.toString(), socket);
//                SocketMap.connection.put(readInstruction.toString(), this);
//                result = subDevicesServer.setAllSubDevicesList(readInstruction.toString());
//                if (result) {
//                    FileUtil.writeLog("rs485", "RS485设备连接成功：设备唯一编号" + readInstruction.toString());
//                }
//            } else {
//                try {
//                    socket.close();
//                } catch (Exception e) {
//
//                }
//
//                log.info("不是自定义设备" + readInstruction.toString());
//                result = false;
//                this.socket = socket;
//                this.deviceNo = "";
//            }
//
//        }
//        time = System.currentTimeMillis();
//
//    }
//
//    public void setInstruction(byte[] instruction, String brand) {
//
//        try {
//            while (!flag) {
//                Thread.sleep(5);
//            }
//            flag = false;
//            log.info(brand + ":开始发送指令" + instruction.toString());
//            this.instruction = instruction;
//            this.brand = brand;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /*读取socket传输信息*/
//    public void run() {
//        OutputStream outputStream = null;
//        InputStream input = null;
//        try {
//            outputStream = socket.getOutputStream();
//            input = socket.getInputStream();
//            while (socket.isConnected()) {
//                /**
//                 * 发送指令时间
//                 */
//                long sendTime = System.currentTimeMillis();
//                /**
//                 * 设备长时间没返回信息为false
//                 */
//                boolean longTimeAccept = true;
//                /**
//                 * 还未收到客户端数据
//                 */
//                boolean read = false;
//                /**
//                 * 是否发送指令
//                 */
//                boolean send = true;
//                /**
//                 * 指令长度
//                 */
//                int length = 0;
//
//                int count = input.available();
//                while (count == 0) {
//                    Thread.sleep(10);
//                    count = input.available();
//                    if (instruction != null && send) {
//                        sendTime = System.currentTimeMillis();
//                        outputStream.write(instruction);
//                        send = false;
//                    }
//                    if (instruction != null) {
//                        long nowTime = System.currentTimeMillis();
//                        long waitTime = nowTime - sendTime;
//
//                        if (waitTime >= 5000l) {//发送指令后2秒没数据
//                            log.info("发送指令后规定时间范围内无数据" + brand + ":nowTime:" + nowTime + "sendTime:" + sendTime + "waitTime:" + waitTime + "instruction" + ByteUtil.bytesToHex(instruction));
//                            instruction = null;
//                            brand = null;
//                            flag = true;
//                            break;
//                        }
//                    }
//
//                    long waitTime = System.currentTimeMillis() - time;
//                    if (waitTime > waitMaxTime) {//2分钟没收到数据即无效socket
//                        longTimeAccept = false;
//                        break;
//                    }
//                    if (count > 0) {
//                        break;
//                    }
//                }
//                StringBuffer sb = new StringBuffer();
//
//                if (longTimeAccept && count > 0) {
//                    while (read) {
//                        if (count != 0) {
//                            System.out.println("count:" + count);
//                            int readCount = 0;
//                            while (readCount < count) {
//                                readCount++;
//                                String hex = Integer.toHexString(input.read()).toUpperCase();
//                                if (hex.length() == 1) {
//                                    sb.append("0" + hex);
//                                } else {
//                                    sb.append(hex);
//                                }
//                            }
//                        }
//                        if (sb.toString().length() > 17) {
//                            String lengthString = sb.toString().substring(12, 16);
//                            length = (Integer.parseInt(lengthString, 16) + 4) * 2;
//                        }
//                        long startTime = System.currentTimeMillis();
//                        count = input.available();
//                        while (count == 0) {
//                            Thread.sleep(10);
//                            count = input.available();
//                            if (System.currentTimeMillis() - startTime > 2000) {
//                                read = false;
//                                break;
//                            }
//                            if (count > 0) {
//                                break;
//                            }
//
//                        }
//                        if (sb.toString().length() >= length) {
//                            break;
//                        }
//                    }
//
//                    StringBuffer readInstruction = null;
//                    Integer address = 0;
//                    String addressText = "";
//
//                    log.info("addressText:" + addressText);
//                    sendRun(readInstruction.toString(), deviceNo, address, brand, instruction.toString(), addressText);
//                    if (!readInstruction.toString().equals(deviceNo)) {
//                        log.info("brand:" + brand + ":flag:" + flag + ":readInstruction:" + readInstruction.toString());
//                        instruction = null;
//                        brand = null;
//                        flag = true;
//
//                    }
//                    time = System.currentTimeMillis();
//                } else if (!longTimeAccept) {
//                    break;
//                }
//                if (sb != null) {
//                    String readInstruction = sb.toString();
//                    sendRun(readInstruction, deviceNo, null, brand, instruction.toString(), sb.substring(0, 2));
//                    if (!readInstruction.equals(deviceNo)) {
//                        log.info("brand:" + brand + ":flag:" + flag + ":readInstruction:" + readInstruction.toString());
//                        instruction = null;
//                        brand = null;
//                        flag = true;
//
//                    }
//                    time = System.currentTimeMillis();
//                }
//
//            }
//
//        } catch (Exception exception) {
//            log.info(" 异常");
//            flag = true;
//            log.error(exception.getMessage(), exception);
//        } finally {
//            try {
//                flag = true;
//                input.close();
//                outputStream.close();
//                log.info(socket.getRemoteSocketAddress() + "断开连接");
//                socket.close();
//            } catch (IOException ioException) {
//                log.error(ioException.getMessage(), ioException);
//            }
//        }
//    }
//
//
//    public boolean isResult() {
//        return result;
//    }
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void sendRun(String readInstruction, String deviceNo, Integer address, String brand, String sendInstruction, String addressText) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (StringUtils.isNotBlank(readInstruction) && StringUtils.isNotBlank(deviceNo)) {
//                        ReadSocketData.read(deviceNo, address, readInstruction, brand, sendInstruction, addressText);
//                        time = System.currentTimeMillis();
//                    }
//                } catch (Exception e) {
//
//                }
//
//            }
//        }).start();
//
//    }
//
//}

package com.qst.dms.service;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.sql.Timestamp;
import java.sql.ResultSet;

import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
import com.qst.dms.entity.Transport;
import com.qst.dms.db.DBUtil;

public class TransportService {
	// 物流数据采集
	public Transport inputTransport() {
		Transport trans = null;

		// 建立一个从键盘接收数据的扫描器
		Scanner scanner = new Scanner(System.in);
		try{
			// 提示用户输入ID标识
			System.out.println("请输入ID标识：");
			// 接收键盘输入的整数
			int id = scanner.nextInt();
			// 获取当前系统时间
			Date nowDate = new Date();
			// 提示用户输入地址
			System.out.println("请输入地址：");
			// 接收键盘输入的字符串信息
			String address = scanner.next();
			// 数据状态是“采集”
			int type = DataBase.GATHER;

			// 提示用户输入登录用户名
			System.out.println("请输入货物经手人：");
			// 接收键盘输入的字符串信息
			String handler = scanner.next();
			// 提示用户输入主机IP
			System.out.println("请输入 收货人:");
			// 接收键盘输入的字符串信息
			String reciver = scanner.next();
			// 提示用于输入物流状态
			System.out.println("请输入物流状态：1发货中，2送货中，3已签收");
			// 接收物流状态
			int transportType = scanner.nextInt();
			// 创建物流信息对象
			trans = new Transport(id, nowDate, address, type, handler, reciver,
					transportType);
		} catch (Exception e) {
			System.out.println("采集的日志信息不合法");
		}
		// 返回物流对象
		return trans;
	}

	// 物流信息输出
	public void showTransport(Transport... transports) {
		for (Transport e : transports) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配的物流信息输出，可变参数
	public void showMatchTransport(MatchedTransport... matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}
	// 匹配的物流信息输出，参数是集合
	public void showMatchTransport(ArrayList<MatchedTransport> matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}
    public void saveMatchedTransport(ArrayList<MatchedTransport> m2) {
        ArrayList<MatchedTransport> matchTrans = readMatchedTransport();
        if (matchTrans != null) {
            matchTrans.addAll(m2);
        } else {
            matchTrans = m2;
        }
        try (ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream("MatchedTransports.txt", false))) {
            for (MatchedTransport e : matchTrans) {
                if (e != null) {
                    obs.writeObject(e);
                    obs.flush();
                }
            }
            obs.writeObject(null);
            obs.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<MatchedTransport> readMatchedTransport() {
        ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("MatchedTransports.txt"))) {
            MatchedTransport matchTran;
            while ((matchTran = (MatchedTransport) ois.readObject()) != null) {
                matchTrans.add(matchTran);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchTrans;
    }
    public void saveMatchTransportToDB(ArrayList<MatchedTransport> matchTrans) {
        DBUtil db = new DBUtil();
        try {
            db.getConnection();
            for (MatchedTransport matchedTransport : matchTrans) {
                Transport send = matchedTransport.getSend();
                Transport trans = matchedTransport.getTrans();
                Transport receive = matchedTransport.getReceive();
                String sql = "insert into gather_transport(id, time, address, type, handler, reciver, transporttype) values (?,?,?,?,?,?,?)";
                Object[] param = new Object[] {
                    send.getId(), new Timestamp(send.getTime().getTime()),send.getAddress(),send.getType(),send.getHandler(),send.getReciver(),send.getTransportType()
                };
                db.executeUpdate(sql, param);
                param          = new Object[] {
                    trans.getId(), new Timestamp(trans.getTime().getTime()),trans.getAddress(),trans.getType(),trans.getHandler(),trans.getReciver(),trans.getTransportType()
                };
                db.executeUpdate(sql, param);
                param          = new Object[] {
                    receive.getId(), new Timestamp(receive.getTime().getTime()),receive.getAddress(),receive.getType(),receive.getHandler(),receive.getReciver(),receive.getTransportType()
                };
                db.executeUpdate(sql, param);
                sql = "insert into matched_transport(sendid, transid,receiveid) values (?,?,?)";
                param = new Object[] {
                    send.getId(),trans.getId(),receive.getId()
                };
                db.executeUpdate(sql, param);
            }
            db.closeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<MatchedTransport> readMatchedTransportFromDB() {
        ArrayList<MatchedTransport> matchedTransports = new ArrayList<MatchedTransport>();
        DBUtil db = new DBUtil();
        try {
            db.getConnection();
            String sql = "select s.id, s.time, s.address, s.type, s.handler, s.reciver, s.transporttype,";
            sql       += "       t.id, t.time, t.address, t.type, t.handler, t.reciver, t.transporttype,";
            sql       += "       r.id, t.time, t.address, t.type, r.handler, r.reciver, r.transporttype ";
            sql += " from matched_transport m, gather_transport s, gather_transport t, gather_transport r ";
            sql += " where m.sendid=s.id and m.transid=t.id and m.receiveid=r.id ";
            ResultSet rs = db.executeQuery(sql, null);
            while (rs.next()) {
                Transport send    = new Transport(rs.getInt(1), rs.getDate(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getInt(7));
                Transport trans   = new Transport(rs.getInt(8), rs.getDate(9), rs.getString(10),rs.getInt(11),rs.getString(12),rs.getString(13),rs.getInt(14));
                Transport receive = new Transport(rs.getInt(15),rs.getDate(16),rs.getString(17),rs.getInt(18),rs.getString(19),rs.getString(20),rs.getInt(21));
                MatchedTransport matchedTrans = new MatchedTransport(send, trans, receive);
                matchedTransports.add(matchedTrans);
            }
            db.closeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchedTransports;
    }
}

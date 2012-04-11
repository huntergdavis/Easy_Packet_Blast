package com.hunterdavis.easypacketblast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasyPacketBlast extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		OnClickListener packetListener = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				SendPackets(v.getContext());
				;
			}
		};
		Button send = (Button) findViewById(R.id.sendButton);
		send.setOnClickListener(packetListener);

		Spinner spinner = (Spinner) findViewById(R.id.numberspin);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.packetchunks,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		// spinner.setOnItemSelectedListener(new
		// MyUnitsOnItemSelectedListener());
		spinner.setSelection(0);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());

	}

	public void SendPackets(Context context) {
		EditText addressText = (EditText) findViewById(R.id.destination);
		String destination = addressText.getText().toString();

		EditText portText = (EditText) findViewById(R.id.port);
		int port = Integer.valueOf(portText.getText().toString());

		EditText payloadText = (EditText) findViewById(R.id.payloadcustomtext);
		String payloadTextValue = payloadText.getText().toString();

		if (destination.length() < 1) {
			Toast.makeText(context, "Invalid IP", Toast.LENGTH_SHORT).show();
			return;
		}
		InetAddress address;

		try {
			address = InetAddress.getByName(destination);
		} catch (UnknownHostException e) {
			Toast.makeText(context, "Unknown Host", Toast.LENGTH_SHORT).show();
			return;
		}

		int loopNumber = getNumberFromSpinner();
		for (int i = 0; i < loopNumber; i++) {
			sendPacket(context, destination, port, payloadTextValue);
		}
		Toast.makeText(context, "Packet(s) Successfully Broadcast",
				Toast.LENGTH_LONG).show();
		return;
	}

	public void sendPacket(Context context, String ipString, int port,
			String customPayload) {

		try {
			int cpl = customPayload.length();
			byte[] bytes = null;
			if (cpl > 0) {
				bytes = customPayload.getBytes("ASCII");
			} else {
				bytes = new byte[6 + 16 * 16 * 16];
				// /for (int i = 0; i < 6; i++) {
				// bytes[i] = (byte) 0xff;
				// }
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = 1;
				}
			}

			InetAddress address;
			try {
				address = InetAddress.getByName(ipString);
			} catch (Exception e) {
				Toast.makeText(context, "Unknown Host", Toast.LENGTH_SHORT)
						.show();
				return;

			}
			// why is port 7???
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
					address, port);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();

		} catch (Exception e) {
			return;
		}

	}

	public int getNumberFromSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.numberspin);
		int spinnersel = spinner.getSelectedItemPosition();
		switch (spinnersel) {
		case 0:
			return 1;
		case 1:
			return 10;
		case 2:
			return 100;
		case 3:
			return 1000;
		case 4:
			return 10000;
		case 5:
			return 100000;
		default:
			return 1;
		}
	}

}
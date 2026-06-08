const { Client, LocalAuth } = require('whatsapp-web.js');
const express = require('express');
const qrcode  = require('qrcode-terminal');

const app  = express();
app.use(express.json());

let isReady = false;

// ── WhatsApp Client ──
const client = new Client({
  authStrategy: new LocalAuth({ dataPath: './.wwebjs_auth' }),
  puppeteer: {
    headless: true,
    executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  }
});

client.on('qr', (qr) => {
  console.log('\n========================================');
  console.log('  Scan this QR code with WhatsApp:');
  console.log('  (Open WhatsApp → Linked Devices → Link a Device)');
  console.log('========================================\n');
  qrcode.generate(qr, { small: true });
});

client.on('ready', () => {
  isReady = true;
  console.log('\n✅ WhatsApp connected and ready to send messages!\n');
});

client.on('disconnected', (reason) => {
  isReady = false;
  console.log('❌ WhatsApp disconnected:', reason);
});

client.initialize();

// ── Helper: format phone for WhatsApp ──
function formatPhone(phone) {
  if (!phone) return null;
  phone = phone.trim().replace(/[\s\-()]/g, '');
  if (phone.startsWith('+')) phone = phone.substring(1);
  if (phone.startsWith('0'))  phone = '94' + phone.substring(1); // Sri Lanka
  return phone + '@c.us';
}

// ── Core send function ──
async function sendWhatsApp(phone, message) {
  if (!isReady) throw new Error('WhatsApp not connected yet');
  const chatId = formatPhone(phone);
  if (!chatId) throw new Error('Invalid phone number');

  // Use getNumberId to resolve the correct WhatsApp ID
  const numberId = await client.getNumberId(chatId.replace('@c.us', ''));
  if (!numberId) throw new Error('Phone number not registered on WhatsApp: ' + phone);

  await client.sendMessage(numberId._serialized, message);
  console.log(`✅ Message sent to ${phone} (${numberId._serialized})`);
}

// ── Health check ──
app.get('/health', (req, res) => {
  res.json({ status: isReady ? 'READY' : 'NOT_READY', clientReady: isReady, timestamp: new Date().toISOString() });
});

// ── Send message with QR image (base64) ──
app.post('/send-qr', async (req, res) => {
  const { phone, message, qrBase64 } = req.body;
  if (!isReady) return res.status(503).json({ success: false, error: 'WhatsApp not connected' });
  if (!phone) return res.status(400).json({ success: false, error: 'phone required' });
  try {
    await sendWhatsApp(phone, message || 'Your FlexiWork QR code:');
    if (qrBase64) {
      const { MessageMedia } = require('whatsapp-web.js');
      const media = new MessageMedia('image/png', qrBase64, 'qr-code.png');
      const chatId = formatPhone(phone);
      const numberId = await client.getNumberId(chatId.replace('@c.us', ''));
      if (numberId) {
        await client.sendMessage(numberId._serialized, media, { caption: '🔲 Scan this QR at the gate' });
      }
    }
    res.json({ success: true, to: phone, type: 'qr' });
  } catch (err) {
    console.error(`❌ QR send failed for ${phone}:`, err.message);
    res.status(500).json({ success: false, error: err.message });
  }
});

// ── Send raw message ──
app.post('/send', async (req, res) => {
  const { phone, message } = req.body;
  if (!isReady) return res.status(503).json({ success: false, error: 'WhatsApp not connected' });
  if (!phone || !message) return res.status(400).json({ success: false, error: 'phone and message required' });
  try {
    await sendWhatsApp(phone, message);
    res.json({ success: true, to: phone });
  } catch (err) {
    console.error(`❌ Failed to send to ${phone}:`, err.message);
    res.status(500).json({ success: false, error: err.message });
  }
});

// ── Check-in message ──
app.post('/checkin', async (req, res) => {
  const { phone, workerName, jobTitle, location, time } = req.body;
  if (!isReady) return res.status(503).json({ success: false, error: 'WhatsApp not connected' });
  if (!phone) return res.status(400).json({ success: false, error: 'phone required' });

  const message =
    `✅ *FlexiWork — Check-In Confirmed!*\n\n` +
    `Hello *${workerName}*,\n\n` +
    `📌 Job: ${jobTitle}\n` +
    `🏭 Location: ${location}\n` +
    `🕐 Check-in Time: ${time}\n\n` +
    `Have a great shift! 💪`;

  try {
    await sendWhatsApp(phone, message);
    res.json({ success: true, to: phone, type: 'checkin' });
  } catch (err) {
    console.error(`❌ Check-in message failed for ${phone}:`, err.message);
    res.status(500).json({ success: false, error: err.message });
  }
});

// ── Check-out message ──
app.post('/checkout', async (req, res) => {
  const { phone, workerName, jobTitle, location, time } = req.body;
  if (!isReady) return res.status(503).json({ success: false, error: 'WhatsApp not connected' });
  if (!phone) return res.status(400).json({ success: false, error: 'phone required' });

  const message =
    `🏁 *FlexiWork — Shift Ended!*\n\n` +
    `Hello *${workerName}*,\n\n` +
    `📌 Job: ${jobTitle}\n` +
    `🏭 Location: ${location}\n` +
    `🕐 Check-out Time: ${time}\n\n` +
    `Great work today! Your payment will be processed shortly. 💰`;

  try {
    await sendWhatsApp(phone, message);
    res.json({ success: true, to: phone, type: 'checkout' });
  } catch (err) {
    console.error(`❌ Check-out message failed for ${phone}:`, err.message);
    res.status(500).json({ success: false, error: err.message });
  }
});

// ── Start server ──
const PORT = 3001;
app.listen(PORT, () => {
  console.log(`\n🚀 FlexiWork WhatsApp Service running on http://localhost:${PORT}`);
  console.log('⏳ Waiting for WhatsApp QR code...\n');
});

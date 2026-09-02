import "./globals.css";

// TODO (Sprint 4): fetch theme_config của tenant hiện tại (dựa vào user đăng nhập)
// rồi set CSS variable --primary-color/--secondary-color động ở đây thay vì giá trị mặc định trong globals.css.

export const metadata = {
  title: "BarOps",
  description: "POS + Inventory cho bar/pub",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="vi">
      <body>{children}</body>
    </html>
  );
}

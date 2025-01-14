function App() {
  const appName = import.meta.env.VITE_APP_NAME;
  console.log(`apiUrl : ${appName}`);

  return (
    <>
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-3xl">Hello world</div>
      </div>
    </>
  );
}

export default App;

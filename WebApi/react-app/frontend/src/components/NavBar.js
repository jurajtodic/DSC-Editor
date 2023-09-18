import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTable } from "@fortawesome/free-solid-svg-icons";
import logo from "../assets/mathoslogo.png";

function NavBar() {
  return (
    <nav className="flex flex-row p-3 bg-slate-400 items-center justify-between rounded-b-lg py-2">
      <section className="flex flex-row items-center justify-between">
        <img src={logo} alt="" className="App-logo w-12 h-12" />
        <Link
          to={"/"}
          className="text-black no-underline rounded-full bg-slate-500 text-lg px-2 ml-3 hover:scale-105 duration-150"
        >
          Tablice <FontAwesomeIcon icon={faTable} />
        </Link>
      </section>
      <section className="text-2xl">Domain Configuration Editor</section>
    </nav>
  );
}
export default NavBar;

import React, { useEffect } from "react";
import Editor, { useMonaco } from "@monaco-editor/react";
import { useState, useRef } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";
import "../style/App.css";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";

function DscEditor() {
  const location = useLocation();
  const { Id } = location.state;
  const { DomainType } = location.state;
  const { DomainName } = location.state;
  const { Activity } = location.state;

  const [dscContent, setDscContent] = useState("");
  const [active, setActive] = useState(Activity);
  const [validateMessage, setValidateMessage] = useState("");
  const [saveMessage, setSaveMessage] = useState("");

  const monaco = useMonaco();
  const editorRef = useRef(null);
  const navigate = useNavigate();
  let markers = [];

  useEffect(() => {
    getConfiguration();
  },[]);

  useEffect(() => {
    if (validateMessage != "") {
      validateToast();
    }
  }, [validateMessage]);

  useEffect(() => {
    if (saveMessage != "") {
      saveToast();
      if (saveMessage == "Domain Configuration save successfully!") {
        setTimeout(() => navigate("/"), 1500);
      }
    }
  }, [saveMessage]);

  function handleEditorWillMount(monaco) {
    // do something before editor is mounted
    monaco.languages.register({ id: "dscLang" });
    monaco.languages.setMonarchTokensProvider("dscLang", {
      bracketPairColorization: true,
      tokenizer: {
        root: [
          [/#.*/, "comment"],
          [/".*?"/, "string"],
          [/(\S+\s)(=)/, ["naming", "eq"]],
          [/(\S+\s)({)/, ["naming", "brackets"]],
        ],
      },
    });

    //zagrade
    const config = {
      brackets: [
        ["{", "}"],
        ["(", ")"],
        ["[", "]"],
      ],
      surroundingPairs: [
        { open: "{", close: "}" },
        { open: "[", close: "]" },
        { open: "(", close: ")" },
        { open: "<", close: ">" },
        { open: "'", close: "'" },
        { open: '"', close: '"' },
      ],
      autoClosingPairs: [
        { open: "{", close: "}" },
        { open: "[", close: "]" },
        { open: "(", close: ")" },
        { open: "'", close: "'", notIn: ["string", "comment"] },
        { open: '"', close: '"', notIn: ["string", "comment"] },
      ],
    };
    monaco.languages.setLanguageConfiguration("dscLang", config);

    //Nova tema
    monaco.editor.defineTheme("dscLang-theme", {
      base: "vs-dark",
      inherit: true,
      folding: true,
      rules: [
        { token: "string", foreground: "#F87475" },
        { token: "comment", foreground: "#AA852E", fontStyle: "italic" },
        { token: "naming", foreground: "#AD4A81" },
      ],
      colors: {
        "editor.background": "#292929",
        "editorCursor.foreground": "#B8B8B8",
        "editor.lineHighlightBackground": "#383838",
        "editorLineNumber.foreground": "#585858",
        "editor.selectionBackground": "#393939",
        "editor.inactiveSelectionBackground": "#717171", 
      },
    });
  }

  function handleEditorDidMount(editor, monaco) {
    // here is the editor instance
    // you can store it in `useRef` for further usage
    editorRef.current = editor;
  }

  const getConfiguration = () => {
    axios
      .get(`/api/v1/configurations/${Id}`)
      .then((res) => {
        // Base64 string
        var content = res.data.content;

        // Text from base64
        var text = decodeURIComponent(window.atob(content));
        setDscContent(text);
      })
      .catch((err) => console.log(err.toJSON()));
  };

  const validateConfiguration = () => {
    const base64content = btoa(dscContent);

    var file = b64toBlob(base64content, "application/octet-stream");

    var formData = new FormData();
    formData.append("file", file, file.name);

    axios
      .post("/api/v1/configurations/validate", formData)
      .then((res) => {
        if (res.data.length > 0) {
          setValidateMessage("Domain Configuration is NOT valid!");
          res.data.forEach((i) => {
            markers.push({
              startLineNumber: i.line,
              endLineNumber: i.line,
              startColumn: i.charPositionInLine + 1,
              endColumn: i.charPositionInLine + i.hint.length + 1,
              message: i.message,
              severity: monaco.MarkerSeverity.Error,
            });
          });
          // markers.push({
          //   startLineNumber: res.data.line,
          //   endLineNumber: res.data.line,
          //   startColumn: res.data.charPositionInLine + 1,
          //   endColumn: res.data.charPositionInLine + res.data.hint.length + 1,
          //   message: "There is a problem with your code",
          //   severity: monaco.MarkerSeverity.Error,
          // });
        } else {
          setValidateMessage("Domain Configuration is valid!");
        }
        monaco.editor.setModelMarkers(
          editorRef.current.getModel(),
          "owner",
          markers
        );
      })
      .catch((err) => console.log(err));
  };

  const saveConfiguration = () => {
    const base64content = btoa(dscContent);

    var file = b64toBlob(base64content, "application/octet-stream");
    console.log("blob from base64", file);

    var formData = new FormData();
    formData.append("file", file, file.name);
    formData.append("type", DomainType);
    formData.append("name", DomainName);
    formData.append("active", active);

    axios
      .post("/api/v1/configurations/", formData)
      .then((res) => {
        setSaveMessage("Domain Configuration save successfully!");
      })
      .catch((err) => {
        console.log(err);
        setSaveMessage("Domain Configuratin save unsuccessfully!");
      });
  };

  const b64toBlob = (b64Data, contentType, sliceSize) => {
    contentType = contentType || "";
    sliceSize = sliceSize || 512;

    var byteCharacters = atob(b64Data);
    var byteArrays = [];

    for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
      var slice = byteCharacters.slice(offset, offset + sliceSize);

      var byteNumbers = new Array(slice.length);
      for (var i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }

      var byteArray = new Uint8Array(byteNumbers);

      byteArrays.push(byteArray);
    }

    console.log(byteArrays);

    return new File(byteArrays, makeid(20), { type: contentType });
  };

  // Get random string
  const makeid = (length) => {
    var result = "";
    var characters =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    var charactersLength = characters.length;
    for (var i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  };

  const validateToast = () => toast(<div>{validateMessage}</div>);

  const activationToast = () =>
    toast(
      <div>
        {active == true
          ? "Domain Configuration have been deactivated!"
          : "Domain Configuration have been activated!"}
      </div>
    );

  const saveToast = () => toast(<div>{saveMessage}</div>);

  return (
    <div className="Editor ">
      <Toaster
        toastOptions={{
          className:
            "p-2  bg-gray rounded-full text-[1.5vh] text-dark hover:scale-105 duration-150",
        }}
      />
      <Editor
        height="80vh"
        defaultLanguage="dscLang"
        defaultValue={dscContent}
        value={dscContent}
        theme="dscLang-theme"
        onChange={(value) => {
          setDscContent(value);
        }}
        beforeMount={handleEditorWillMount}
        onMount={handleEditorDidMount}
      />
      <div className="flex justify-between">
        <button
          type="button"
          className=" p-2  bg-gray-600 rounded-full text-[2vh] text-white hover:scale-105 duration-150 "
          onClick={saveConfiguration}
        >
          Save Configuration!
        </button>
        <button
          type="button"
          className=" p-2  bg-gray-600 rounded-full text-[2vh] text-white hover:scale-105 duration-150 "
          onClick={() => {
            validateConfiguration();
          }}
        >
          Validate Configuration!
        </button>
        <button
          type="button"
          className=" p-2  bg-gray-600 rounded-full text-[2vh] text-white hover:scale-105 duration-150 "
          onClick={() => {
            setActive((prev) => !prev);
            activationToast();
          }}
        >
          Activate Configuration!
        </button>
      </div>
    </div>
  );
}

export default DscEditor;

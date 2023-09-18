import React, { useMemo, useState, useEffect } from "react";
import { Link } from "react-router-dom";

import DataTable from "react-data-table-component";
import FilterComponent from "./FilterComponent";

const Table = () => {
  const [fetchedData, setFetchedData] = useState([]);
  const [filterText, setFilterText] = useState("");
  const [resetPaginationToggle, setResetPaginationToggle] = useState(false);

  //FETCHING CONFIGURATIONS
  const handleFetchAllDomains = async () => {
    fetch("api/v1/configurations?pageSize=100")
      .then((res) => res.json())
      .then((data) => {
        setFetchedData(data.content);
      });
  };

  //FETCHING ALL THE DOMAINS ON MOUNTING
  useEffect(() => {
    handleFetchAllDomains();
  }, []);

  //EDIT BUTTON FOR SENDING INFO TO MONACO
  const editButton = (id, domainType, domainName, activity) => (
    <Link
      to={"DscEdit/" + id}
      state={{
        Id: id,
        DomainType: domainType,
        DomainName: domainName,
        Activity: activity,
      }}
    >
      <button className=" p-2  bg-gray-600 rounded-full text-xs text-white hover:scale-105 duration-75 ">
        Edit Configuration
      </button>
    </Link>
  );

  //COLUMNS FOR TABLE
  const columns = [
    {
      name: "DomainId",
      selector: (row) => row.id,
      sortable: true,
    },
    {
      name: "DomainType",
      selector: (row) => row.type,
      sortable: true,
    },
    {
      name: "DomainName",
      selector: (row) => row.name,
      sortable: true,
    },
    {
      name: "Activity",
      selector: (row) => {
        return (
          <label>
            <input
              type="checkbox"
              disabled="disabled"
              defaultChecked={row.active}
            />
          </label>
        );
      },
    },
    {
      name: "Edit",
      cell: (row) => editButton(row.id, row.type, row.name, row.active),
    },
  ];

  //CREATING FILTER COMPONENT ON TOP AND HANDLING PAGINATION TOGGLE
  const subHeaderComponent = useMemo(() => {
    const handleClear = () => {
      if (filterText) {
        setResetPaginationToggle(!resetPaginationToggle);
        setFilterText("");
      }
    };
    return (
      <FilterComponent
        onFilter={(e) => setFilterText(e.target.value)}
        onClear={handleClear}
        filterText={filterText}
      />
    );
  }, [filterText, resetPaginationToggle]);

  //FILTERING DATA
  const filteredItems = fetchedData.filter(
    (item) =>
      JSON.stringify(item).toLowerCase().indexOf(filterText.toLowerCase()) !==
      -1
  );

  return (
    <DataTable
      title="Domain Configurations"
      columns={columns}
      data={filteredItems}
      defaultSortField="DomainId"
      striped
      pagination
      subHeader
      subHeaderComponent={subHeaderComponent}
    />
  );
};

export default Table;

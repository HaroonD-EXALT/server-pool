function maxId(stream)
    -- The stream function cannot return record objects directly,
    -- so we have to map to a Map data type first.
    local function toArray(rec)
        local result = map()
        print(rec)
        if rec == nil then
            result['PK'] = 0
            return result
        end

        result['PK'] = rec['PK']
        return result
    end
    local function findMax(a, b)
        if a.PK > b.PK then
            return a
        else
            return b
        end
    end
    return stream : map(toArray) : reduce(findMax)
end